from django.contrib.auth import get_user_model
from django.db import models
from django.core.validators import MinValueValidator, MaxValueValidator
import uuid
import threading
from enum import IntEnum
import csv


class OhaSensor(models.Model):

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

    def get_sensor_converted_value(self, sensor_value):
        sensor_value = float(sensor_value)
        return self.default_convection * sensor_value

    def get_recent_log(self):
        if (self.sensor_type == self.Types.ENERGY_LOG):
            lastEnergyLog = OhaEnergyLog.objects.filter(
                oha_sensor=self).last()
            return lastEnergyLog
        return None
    
    def import_csv(self, csv_file_path):
        with open(csv_file_path) as csvfile:
            csvReader = csv.DictReader(csvfile)
            for row in csvReader:
                energy_log = OhaEnergyLog(oha_sensor=self,  unix_time=row['unix_time'], duration=row['duration'], voltage=self.default_volts, watts1=row['watts1'], watts2=row['watts2'], watts3=row['watts3'], watts_total=row['watts_total'], sensor_convection=row['sensor_convection'])
                OhaEnergyLog.objects.filter(oha_sensor=self, unix_time=row['unix_time']).delete()
                energy_log.save()
                print("Saved:",row['unix_time'])
            print("import_csv finished")

class OhaSensorDimDate(models.Model):
    oha_sensor = models.ForeignKey(OhaSensor, on_delete=models.CASCADE)
    year = models.PositiveIntegerField(validators=[MinValueValidator(1900)])
    month = models.PositiveIntegerField(validators=[MinValueValidator(1), MaxValueValidator(12)])
    day = models.PositiveIntegerField(validators=[MinValueValidator(1), MaxValueValidator(31)])
    hour = models.PositiveIntegerField(validators=[MinValueValidator(0), MaxValueValidator(23)])
    day_of_week = models.PositiveIntegerField(validators=[MinValueValidator(0), MaxValueValidator(6)])
    date_time = models.DateTimeField()
    
    class Meta:
        indexes = [models.Index(
            fields=['oha_sensor', 'date_time', ], name="idx_dim_date_sensor_date_time"), ]
        unique_together = ('oha_sensor', 'year','month','day','hour')

    def get_or_create(oha_sensor, unix_time):
        unix_time_to_date = datetime.datetime.utcfromtimestamp(unix_time)
        dt_as_tz = unix_time_to_date.astimezone(pytz.timezone(oha_sensor.time_zone))
        year, month, day, hour = [dt_as_tz.year, dt_as_tz.month, dt_as_tz.day, dt_as_tz.hour]
        dim_date = OhaSensorDimDate.objects.get(oha_sensor=oha_sensor,year=year, month=month, day=day, hour=hour )
        if not dim_date.pk:
            dim_date = OhaSensorDimDate(oha_sensor=oha_sensor, year=year, month=month, day=day, hour=hour, dat)
            dim_date.save()
        return dim_date

class OhaSensorLogBatch(models.Model):
    oha_sensor = models.ForeignKey(OhaSensor, on_delete=models.CASCADE)
    secret_api_token = models.UUIDField()
    content = models.TextField(default="1574608324;10;1;2;3", blank=True)

    def do_oha_energy_log_bulk_insert(self):
        oha_sensor = self.oha_sensor
        for log in self.content.split('|'):
            oha_energy_log = OhaEnergyLog()
            if oha_energy_log.parser(oha_sensor, log):
                oha_energy_log.save()

    def save(self, *args, **kwargs):
        # super().save(*args, **kwargs)
        oha_sensor = self.oha_sensor

        if (oha_sensor.sensor_type == OhaSensor.Types.ENERGY_LOG):
            doBulkInsert = threading.Thread(
                target=self.do_oha_energy_log_bulk_insert)
            doBulkInsert.start()


class OhaEnergyLog(models.Model):
    oha_sensor = models.ForeignKey(OhaSensor, on_delete=models.CASCADE)
    unix_time = models.BigIntegerField()
    dim_date = models.ForeignKey(OhaSensorDimDate, on_delete=models.DO_NOTHING)
    duration = models.FloatField()
    voltage = models.IntegerField()
    watts1 = models.FloatField()
    watts2 = models.FloatField()
    watts3 = models.FloatField()
    watts_total = models.FloatField()
    sensor_convection = models.FloatField()

    class Meta:
        unique_together = ('oha_sensor', 'unix_time')

    def save(self, *args, **kwargs):
        if not self.pk :
             OhaEnergyLog.objects.filter(oha_sensor=self.oha_sensor, unix_time=self.unix_time).delete()
        super(OhaEnergyLog , self).save()
        
    def parser(self, oha_sensor, log):
        UNIX_TIME = 0
        DURATION = 1
        SENSOR_PHASE_1 = 2
        SENSOR_PHASE_2 = 3
        SENSOR_PHASE_3 = 4
        FLAG_SEP_COLUMN = ";"
        FLAG_QTD_COLUMN = 5
        log_columns = log.split(FLAG_SEP_COLUMN)
        print("Log:", log)
        if (len(log_columns) == FLAG_QTD_COLUMN):
            self.oha_sensor = oha_sensor
            self.unix_time = log_columns[UNIX_TIME]
            self.duration = log_columns[DURATION]
            self.voltage = oha_sensor.get_default_volts(0)
            self.watts1 = oha_sensor.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_1])
            self.watts2 = oha_sensor.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_2])
            self.watts3 = oha_sensor.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_3])
            self.watts_total = (self.watts1 + self.watts2 + self.watts3)
            self.sensor_convection = oha_sensor.default_convection
            return True
        return False


class OhaEnergyBill(models.Model):
    oha_sensor = models.ForeignKey(
        OhaSensor, on_delete=models.CASCADE)
    period_from = models.BigIntegerField()
    period_to = models.BigIntegerField()
    kwh_cost = models.FloatField()
