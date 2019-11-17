from django.contrib.auth import get_user_model
from django.db import models
from django.db.models import constants
import uuid


class OhaDevice(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    owner = models.ForeignKey(
        get_user_model(),
        on_delete=models.CASCADE
    )
    name = models.CharField(max_length=50)


class OhaEnergyLog(models.Model):
    ohaDeviceId = models.ForeignKey(OhaDevice, on_delete=models.CASCADE)
    ohaEnergyLogId = models.BigIntegerField()
    duration = models.BigIntegerField()
    voltage = models.IntegerField()
    watts1 = models.FloatField()
    watts2 = models.FloatField()
    watts3 = models.FloatField()
    wattsTotal = models.FloatField()
