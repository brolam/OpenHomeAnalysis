from django.contrib.auth import get_user_model
from django.db import models, IntegrityError
from django.core.validators import MinValueValidator, MaxValueValidator
import uuid
from enum import IntEnum
from datetime import datetime
import pytz
import threading
import csv


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
    default_volts = models.IntegerField(default=220)
    default_convection = models.FloatField(default=0.089125)
    secret_api_token = models.UUIDField(default=uuid.uuid4)

    def get_default_volts(self, read_volts):
        return self.default_volts if int(read_volts) == 0 else read_volts

    def get_converted_value(self, sensor_value):
        sensor_value = float(sensor_value)
        return self.default_convection * sensor_value

    def get_recent_logs(self, amount):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            return EnergyLog.objects.filter(sensor=self).order_by('-unix_time')[:amount]
        return None

    def get_between_unix_time_logs(self, start, end):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            return EnergyLog.objects.filter(sensor=self, unix_time__range=(start, end))
        return None

    def import_csv(self, csv_file_path):
        with open(csv_file_path) as csvfile:
            csvReader = csv.DictReader(csvfile)
            for row in csvReader:
                energy_log = EnergyLog(sensor=self,  unix_time=row['unix_time'], duration=row['duration'], voltage=self.default_volts, watts1=row['watts1'],
                                       watts2=row['watts2'], watts3=row['watts3'], watts_total=row['watts_total'], sensor_convection=row['sensor_convection'])
                energy_log.save()
                print("Saved:", row['unix_time'])
            print("import_csv finished")

    def update_logs_dim_time(self):
        logs_to_update = EnergyLog.filter(
            sensor=self).values('pk', 'unix_time')
        for log in logs_to_update:
            print('Log dim_time update:', log)
            EnergyLog.filter(pk=log.pk).update(
                dim_time=DimTime.get_or_create(self, log.unix_time))


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

    class Meta:
        unique_together = ('sensor', 'date_time')

    @staticmethod
    def get_period_of_day(hour):
        if (hour > 4) and (hour <= 8):
            return 0  # 'Early Morning'
        elif (hour > 8) and (hour <= 12):
            return 1  # 'Morning'
        elif (hour > 12) and (hour <= 16):
            return 2  # 'Noon'
        elif (hour > 16) and (hour <= 20):
            return 3  # 'Eve'
        elif (hour > 20) and (hour <= 24):
            return 4  # 'Night'
        elif (hour <= 4):
            return 4  # 'Late Night'

    @staticmethod
    def get_or_create(sensor, unix_time):
        unix_time_as_date = datetime.utcfromtimestamp(unix_time)
        sensor_time_zone = pytz.timezone(sensor.time_zone)
        dt_as_tz = unix_time_as_date.astimezone(sensor_time_zone)
        year, month, day, hour, day_of_week = [
            dt_as_tz.year, dt_as_tz.month, dt_as_tz.day, dt_as_tz.hour, dt_as_tz.weekday()]
        date_time = datetime(year, month, day, hour, 0, 0, 0)
        dim_time = DimTime.objects.filter(
            sensor=sensor, date_time=date_time).first()
        if not dim_time:
            period_of_day = DimTime.get_period_of_day(hour)
            dim_time = DimTime(sensor=sensor, date_time=date_time, year=year, month=month,
                               day=day, hour=hour, day_of_week=day_of_week, period_of_day=period_of_day)
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
    voltage = models.IntegerField()
    watts1 = models.FloatField()
    watts2 = models.FloatField()
    watts3 = models.FloatField()
    watts_total = models.FloatField()
    sensor_convection = models.FloatField()

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
        log_columns = log.split(FLAG_SEP_COLUMN)
        print("Log parser:", log)
        if (len(log_columns) == FLAG_QTD_COLUMN):
            self.sensor = sensor
            self.unix_time = log_columns[UNIX_TIME]
            self.duration = log_columns[DURATION]
            self.voltage = sensor.get_default_volts(0)
            self.watts1 = sensor.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_1])
            self.watts2 = sensor.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_2])
            self.watts3 = sensor.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_3])
            self.watts_total = (self.watts1 + self.watts2 + self.watts3)
            self.sensor_convection = sensor.default_convection
            return True
        return False


class EnergyBill(models.Model):
    sensor = models.ForeignKey(Sensor, on_delete=models.CASCADE)
    period_from = models.BigIntegerField()
    period_to = models.BigIntegerField()
    kwh_cost = models.FloatField()
