import csv
import threading
import uuid
from datetime import datetime
from enum import IntEnum

import pytz
from django.contrib.auth import get_user_model
from django.core.validators import MaxValueValidator, MinValueValidator
from django.db import IntegrityError, models
from django.db.models import F, Sum, Avg


class Sensor(models.Model):

    class Types(IntEnum):
        ENERGY_LOG = 1
        @classmethod
        def choices(cls):
            return [(key.value, key.name) for key in cls]

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    owner = models.ForeignKey(get_user_model(), on_delete=models.CASCADE)
    sensor_type = models.IntegerField(choices=Types.choices())
    name = models.CharField(max_length=50)
    time_zone = models.TextField(max_length=200, default="America/Recife")
    default_convection = models.FloatField(default=13.31229)
    secret_api_token = models.UUIDField(default=uuid.uuid4)

    def get_converted(self, sensor_value):
        sensor_value = float(sensor_value)
        return self.default_convection * sensor_value

    def unix_time_to_datetime(self, unix_time):
        unix_time_as_date = datetime.utcfromtimestamp(unix_time)
        sensor_time_zone = pytz.timezone(self.time_zone)
        dt_as_tz = unix_time_as_date.astimezone(sensor_time_zone)
        return dt_as_tz

    def get_recent_logs(self, amount):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            return EnergyLog.objects.filter(sensor=self, dim_time__day=1).order_by('-unix_time')[:amount]
        return None

    def get_summary_cost_day(self, year, month, day):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            hours = Sum('energylog__duration') / 100
            kwh_total = Sum('energylog__watts_total')
            cost_value = Avg('cost__value')
            values = DimTime.objects.filter(
                sensor=self, year=year, month=month, day=day).aggregate(cost_value=cost_value, kwh_total=kwh_total, hours=hours)
            return {'cost_total': values['kwh_total']}
        return None

    def get_series_by_hour(self, year, month, day):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            hours = Sum('energylog__duration') / 100.00 / 3600.00
            y = Sum('energylog__watts_total') * hours / 1000.00
            return DimTime.objects.filter(sensor=self, year=year, month=month, day=day).values(x=F('hour')).annotate(y=y)
        return None

    def get_logs(self, year, month):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            return EnergyLog.objects.filter(sensor=self, dim_time__year=year, dim_time__month=month)
        return None

    def import_csv(self, csv_file_path):
        with open(csv_file_path) as csvfile:
            csvReader = csv.DictReader(csvfile)
            for row in csvReader:
                energy_log = EnergyLog(sensor=self,  unix_time=row['unix_time'], duration=row['duration'], watts1=row['watts1'],
                                       watts2=row['watts2'], watts3=row['watts3'], watts_total=row['watts_total'], sensor_convection=row['sensor_convection'])
                energy_log.save()
                print("Saved:", row['unix_time'])
            print("import_csv finished")

    def update_logs_from_default_convection(self):
        last_conv = F('sensor_convection')
        watts1 = F('watts1')
        watts2 = F('watts2')
        watts3 = F('watts3')
        watts_total = F('watts_total')
        EnergyLog.objects.filter(sensor=self).exclude(sensor_convection=self.default_convection).update(
            watts1=watts1 / last_conv * self.default_convection,
            watts2=watts2 / last_conv * self.default_convection,
            watts3=watts3 / last_conv * self.default_convection,
            watts_total=watts_total / last_conv * self.default_convection,
            sensor_convection=self.default_convection
        )


class Cost(models.Model):
    sensor = models.ForeignKey(Sensor, on_delete=models.CASCADE)
    title = models.CharField(max_length=15)
    value = models.FloatField()

    @staticmethod
    def get_or_create(sensor):
        cost = Cost.objects.filter(sensor=sensor).last()
        if not cost:
            cost = Cost(sensor=sensor, title='$', value=0.687429)
            cost.save()
        return cost


class DimTime(models.Model):
    class PeriodOfDayTypes(IntEnum):
        EARLY_MORNING = 0
        MORNING = 1
        NOON = 2
        EVE = 3
        NIGHT = 4
        LATE_NIGHT = 5
        @classmethod
        def choices(cls):
            return [(key.value, key.name) for key in cls]

    sensor = models.ForeignKey(Sensor, on_delete=models.CASCADE)
    date_time = models.DateTimeField()
    year = models.PositiveIntegerField(validators=[MinValueValidator(1900)])
    month = models.PositiveIntegerField(
        validators=[MinValueValidator(1), MaxValueValidator(12)])
    day = models.PositiveIntegerField(
        validators=[MinValueValidator(1), MaxValueValidator(31)])
    hour = models.PositiveIntegerField(
        validators=[MinValueValidator(0), MaxValueValidator(23)])
    day_of_week = models.PositiveIntegerField(
        validators=[MinValueValidator(0), MaxValueValidator(6)])
    period_of_day = models.IntegerField(choices=PeriodOfDayTypes.choices())
    cost = models.ForeignKey(Cost, on_delete=models.DO_NOTHING)

    class Meta:
        unique_together = ('sensor', 'date_time')

    @staticmethod
    def get_period_of_day(hour):
        if (hour > 4) and (hour <= 8):
            return DimTime.PeriodOfDayTypes.EARLY_MORNING  # 'Early Morning'
        elif (hour > 8) and (hour <= 12):
            return DimTime.PeriodOfDayTypes.MORNING  # 'Morning'
        elif (hour > 12) and (hour <= 16):
            return DimTime.PeriodOfDayTypes.NOON  # 'Noon'
        elif (hour > 16) and (hour <= 20):
            return DimTime.PeriodOfDayTypes.EVE  # 'Eve'
        elif (hour > 20) and (hour <= 24):
            return DimTime.PeriodOfDayTypes.NIGHT  # 'Night'
        elif (hour <= 4):
            return DimTime.PeriodOfDayTypes.LATE_NIGHT  # 'Late Night'

    @staticmethod
    def get_or_create(sensor, unix_time):
        dt_as_tz = sensor.unix_time_to_datetime(unix_time)
        year, month, day, hour, day_of_week = [
            dt_as_tz.year, dt_as_tz.month, dt_as_tz.day, dt_as_tz.hour, dt_as_tz.weekday()]
        date_time = datetime(year, month, day, hour, 0, 0, 0)
        dim_time = DimTime.objects.filter(
            sensor=sensor, date_time=date_time).first()
        if not dim_time:
            period_of_day = DimTime.get_period_of_day(hour)
            cost = Cost.get_or_create(sensor)
            dim_time = DimTime(sensor=sensor, date_time=date_time, year=year, month=month,
                               day=day, hour=hour, day_of_week=day_of_week, period_of_day=period_of_day, cost=cost)
            dim_time.save()
        return dim_time


class SensorLogBatch(models.Model):
    sensor = models.ForeignKey(Sensor, on_delete=models.CASCADE)
    secret_api_token = models.UUIDField()
    content = models.TextField(
        default="1574608324;10;1;2;3|1574608354;10;1;2;3", blank=True)

    def do_energy_log_bulk_insert(self):
        sensor = self.sensor
        for log in self.content.split('|'):
            energy_log = EnergyLog()
            if energy_log.parser(sensor, log):
                energy_log.save()

    def save(self, *args, **kwargs):
        # super().save(*args, **kwargs)
        sensor = self.sensor
        if (sensor.sensor_type == Sensor.Types.ENERGY_LOG):
            doBulkInsert = threading.Thread(
                target=self.do_energy_log_bulk_insert)
            doBulkInsert.start()


class EnergyLog(models.Model):
    sensor = models.ForeignKey(Sensor, on_delete=models.CASCADE)
    unix_time = models.BigIntegerField()
    dim_time = models.ForeignKey(DimTime, on_delete=models.DO_NOTHING)
    duration = models.FloatField()
    watts1 = models.FloatField()
    watts2 = models.FloatField()
    watts3 = models.FloatField()
    watts_total = models.FloatField()
    sensor_convection = models.FloatField()

    @property
    def datetime(self):
        return self.sensor.unix_time_to_datetime(self.unix_time)

    class Meta:
        unique_together = ('sensor', 'unix_time')

    def save(self, *args, **kwargs):
        self.dim_time = DimTime.get_or_create(self.sensor, int(self.unix_time))
        try:
            super(EnergyLog, self).save()
        except IntegrityError as e:
            if 'UNIQUE constraint' in str(e.args):
                print("e: ", e)
                EnergyLog.objects.filter(
                    sensor=self.sensor, unix_time=int(self.unix_time)).delete()
                super(EnergyLog, self).save()

    def parser(self, sensor, log):
        UNIX_TIME = 0
        DURATION = 1
        SENSOR_PHASE_1 = 2
        SENSOR_PHASE_2 = 3
        SENSOR_PHASE_3 = 4
        FLAG_SEP_COLUMN = ";"
        FLAG_QTD_COLUMN = 5
        values = log.split(FLAG_SEP_COLUMN)
        if (len(values) == FLAG_QTD_COLUMN):
            self.sensor = sensor
            self.unix_time = values[UNIX_TIME]
            self.duration = values[DURATION]
            self.watts1 = sensor.get_converted(values[SENSOR_PHASE_1])
            self.watts2 = sensor.get_converted(values[SENSOR_PHASE_2])
            self.watts3 = sensor.get_converted(values[SENSOR_PHASE_3])
            self.watts_total = (self.watts1 + self.watts2 + self.watts3)
            self.sensor_convection = sensor.default_convection
            return True
        return False
