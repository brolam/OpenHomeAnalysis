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


class OhaEnergyLog(models.Model):
    oha_device_id = models.ForeignKey(OhaDevice, on_delete=models.CASCADE)
    date_time_id = models.BigIntegerField()
    time_zone = models.TextField(max_length=200)
    duration = models.FloatField()
    voltage = models.IntegerField()
    watts1 = models.FloatField()
    watts2 = models.FloatField()
    watts3 = models.FloatField()
    watts_total = models.FloatField()
    class Meta:
        indexes = [models.Index(fields=['oha_device_id', 'date_time_id',], name="unique_key_oha_energy_log"),]

class OhaEnergyLogBatch(models.Model):
    oha_device_id = models.ForeignKey(OhaDevice, on_delete=models.CASCADE)
    content = models.TextField(blank=False)
    
    def do_oha_energy_log_bulk_insert(self):
        print("OhaEnergyLogBatch Content: {}".format( self.content))
    
    def save(self, *args, **kwargs):
        #super().save(*args, **kwargs)
        print("OhaEnergyLogBatch id: {}".format(self.id))
        doBulkInsert = threading.Thread(target=self.do_oha_energy_log_bulk_insert)
        doBulkInsert.start()
    
    
