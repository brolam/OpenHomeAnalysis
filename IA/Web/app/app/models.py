from django.contrib.auth import get_user_model
from django.db import models
import uuid
import threading

class OhaDevice(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    owner = models.ForeignKey(
        get_user_model(),
        on_delete=models.CASCADE
    )
    name = models.CharField(max_length=50)
    time_zone = models.TextField(max_length=200, default="America/Recife")
    last_synced_date = models.IntegerField(default=0)
    last_synced_hour = models.IntegerField(default=0)
    last_synced_sequence = models.BigIntegerField(default=0)
    default_volts = models.IntegerField(default=220)
    default_sensor_convection = models.FloatField(default=0.089125)

    def get_default_volts(self, read_volts):
        return self.default_volts if int(read_volts) == 0 else read_volts

    def get_sensor_converted_value(self, sensor_value):
        sensor_value = float(sensor_value)
        return self.default_sensor_convection * sensor_value

class OhaEnergyLog(models.Model):
    oha_device = models.ForeignKey(OhaDevice, on_delete=models.CASCADE)
    unix_time = models.BigIntegerField()
    duration = models.FloatField()
    voltage = models.IntegerField()
    watts1 = models.FloatField()
    watts2 = models.FloatField()
    watts3 = models.FloatField()
    watts_total = models.FloatField()

    class Meta:
        indexes = [models.Index(
            fields=['oha_device', 'unix_time', ], name="unique_key_oha_energy_log"), ]

    def parser(self, oha_device, log):
        LAST_SYNCED_SEQUENCE = 0
        UNIX_TIME = 1
        DURATION = 2
        VOLTS = 3
        SENSOR_PHASE_1 = 4
        SENSOR_PHASE_2 = 5
        SENSOR_PHASE_3 = 6
        FLAG_SEP_COLUMN = "|"
        FLAG_QTD_COLUMN = 7
        log_columns = log.split(FLAG_SEP_COLUMN)
        log_last_synced_sequence = -1
        print("Log:", log)
        if (len(log_columns) == FLAG_QTD_COLUMN):
            self.oha_device = oha_device
            self.unix_time = log_columns[UNIX_TIME]
            self.duration = log_columns[DURATION]
            self.voltage = oha_device.get_default_volts(log_columns[VOLTS])
            self.watts1 = oha_device.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_1])
            self.watts2 = oha_device.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_2])
            self.watts3 = oha_device.get_sensor_converted_value(
                log_columns[SENSOR_PHASE_3])
            log_last_synced_sequence = int(log_columns[LAST_SYNCED_SEQUENCE])
            self.watts_total = (self.watts1 + self.watts2 + self.watts3)
        return log_last_synced_sequence


class OhaEnergyLogBatch(models.Model):
    oha_device = models.ForeignKey(OhaDevice, on_delete=models.CASCADE)
    current_sync_date = models.IntegerField(default=20181101)
    current_sync_hour = models.IntegerField(default=0)
    content = models.TextField(default="1|1574608324|10|0|1|2|3")

    def do_oha_energy_log_bulk_insert(self):
        oha_device = self.oha_device
        log_last_synced_sequence = -1
        for log in self.content.split(";"):
            oha_energy_log = OhaEnergyLog()
            log_last_synced_sequence = oha_energy_log.parser(oha_device, log)
            OhaEnergyLog.objects.filter(
                oha_device=oha_device, unix_time=oha_energy_log.unix_time
            ).delete()
            oha_energy_log.save()

        if (log_last_synced_sequence > -1):
            oha_device.last_synced_sequence = log_last_synced_sequence
            oha_device.last_synced_date = self.current_sync_date
            oha_device.last_synced_hour = self.current_sync_hour
            oha_device.save()

    def save(self, *args, **kwargs):
        #super().save(*args, **kwargs)
        doBulkInsert = threading.Thread(
            target=self.do_oha_energy_log_bulk_insert)
        doBulkInsert.start()

class OhaEnergyBill(models.Model):
    oha_device = models.ForeignKey(OhaDevice, on_delete=models.CASCADE)
    period_from = models.BigIntegerField()
    period_to = models.BigIntegerField()	
    kwh_cost = models.FloatField()