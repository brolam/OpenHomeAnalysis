from django.contrib.auth.models import User
from .models import OhaDevice, OhaEnergyLog, OhaEnergyLogBatch
from rest_framework import serializers


class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ['url', 'username', 'email', 'groups']


class OhaDeviceSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaDevice
        fields = ['id', 'owner', 'name',]


class OhaEnergyLogSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaEnergyLog
        fields = ['id', 'oha_device_id', 'date_time_id', 'duration', 'voltage', 'watts1', 'watts2', 'watts3', 'watts_total']

class OhaEnergyLogBatchSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaEnergyLogBatch
        fields = ['id', 'oha_device_id', 'content']