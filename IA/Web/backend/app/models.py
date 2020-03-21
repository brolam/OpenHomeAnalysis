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
    default_to_convert = models.FloatField(default=26.378)
    secret_api_token = models.UUIDField(default=uuid.uuid4)

    def get_converted(self, sensor_value):
        sensor_value = float(sensor_value)
        return self.default_to_convert * sensor_value

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
            kwh = Sum(F('energylog__watts_total') *
                      F('energylog__duration') / 3600 / 1000)
            cost = Avg('cost__value')
            values = DimTime.objects.filter(
                sensor=self, year=year, month=month, day=day).aggregate(cost=cost, kwh=kwh)
            return {'cost_total': values['kwh'] * values['cost']}
        return None

    def get_series_by_hour(self, year, month, day):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            y = Sum(F('energylog__watts_total') *
                    F('energylog__duration') / 3600 / 1000)
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
                                       watts2=row['watts2'], watts3=row['watts3'], watts_total=row['watts_total'], sensor_to_convert=row['sensor_to_convert'])
                energy_log.save()
                print("Saved:", row['unix_time'])
            print("import_csv finished")

    def update_logs_from_default_to_convert(self):
        last_conv = F('sensor_to_convert')
        watts1 = F('watts1')
        watts2 = F('watts2')
        watts3 = F('watts3')
        watts_total = F('watts_total')
        EnergyLog.objects.filter(sensor=self).exclude(sensor_to_convert=self.default_to_convert).update(
            watts1=watts1 / last_conv * self.default_to_convert,
            watts2=watts2 / last_conv * self.default_to_convert,
            watts3=watts3 / last_conv * self.default_to_convert,
            watts_total=watts_total / last_conv * self.default_to_convert,
            sensor_to_convert=self.default_to_convert
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
    content = models.TextField(default="1574608324;1;2;3", blank=True)
    attempts = models.PositiveIntegerField(default=0)
    exception = models.TextField(default="", blank=True)

    def do_energy_log_bulk_insert(self):
        sensor = self.sensor
        for log in self.content.split('|'):
            print('Log:', log)
            energy_log = EnergyLog()
            if energy_log.parser(sensor, log):
                energy_log.save()

    def process_last_batch(self):
        sensor = self.sensor
        if (sensor.sensor_type == Sensor.Types.ENERGY_LOG):
            batchs = SensorLogBatch.objects.filter(
                sensor=self.sensor, id__lte=self.id, attempts__lt=3)
            for batch in batchs:
                try:
                    batch.do_energy_log_bulk_insert()
                    batch.delete()
                except Exception as e:
                    batch.exception = str(e)
                    batch.attempts = batch.attempts + 1
                    batch.save()

    def save(self, *args, **kwargs):
        super().save(*args, **kwargs)
        doBulkInsert = threading.Thread(target=self.process_last_batch)
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
    sensor_to_convert = models.FloatField()

    @property
    def datetime(self):
        return self.sensor.unix_time_to_datetime(self.unix_time)

    class Meta:
        unique_together = ('sensor', 'unix_time')

    def save(self, *args, **kwargs):
        self.dim_time = DimTime.get_or_create(self.sensor, int(self.unix_time))
        self.duration = self.calc_duration()
        try:
            super(EnergyLog, self).save()
        except IntegrityError as e:
            if 'UNIQUE constraint' in str(e.args):
                print("e: ", e)
                EnergyLog.objects.filter(
                    sensor=self.sensor, unix_time=int(self.unix_time)).delete()
                super(EnergyLog, self).save()

    def calc_duration(self):
        diff_duration = None
        last_energy_log = EnergyLog.objects.filter(
            sensor=self.sensor, unix_time__lt=int(self.unix_time)).last()
        if last_energy_log:
            diff_duration = int(self.unix_time) - last_energy_log.unix_time
        return diff_duration if (diff_duration is not None and diff_duration < 20) else 20

    def parser(self, sensor, log):
        UNIX_TIME = 0
        SENSOR_PHASE_1 = 1
        SENSOR_PHASE_2 = 2
        SENSOR_PHASE_3 = 3
        FLAG_SEP_COLUMN = ";"
        FLAG_QTD_COLUMN = 4
        values = log.split(FLAG_SEP_COLUMN)
        number_of_columns = len(values)
        if (number_of_columns != FLAG_QTD_COLUMN):
            raise Exception(
                "Invalid number (${number_of_columns}) of columns Log: ${log}")
        self.sensor = sensor
        self.unix_time = values[UNIX_TIME]
        self.watts1 = sensor.get_converted(values[SENSOR_PHASE_1])
        self.watts2 = sensor.get_converted(values[SENSOR_PHASE_2])
        self.watts3 = sensor.get_converted(values[SENSOR_PHASE_3])
        self.watts_total = (self.watts1 + self.watts2 + self.watts3)
        self.sensor_to_convert = sensor.default_to_convert
        return True
