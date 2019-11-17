from django.contrib.auth.models import User
from app.models import OhaDevice, OhaEnergyLog
from rest_framework import serializers


class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ['url', 'username', 'email', 'groups']


class OhaDeviceSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaDevice
        fields = ['id', 'owner', 'name']


class OhaEnergyLogSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaEnergyLog
        fields = ['id', 'ohaDeviceId', 'ohaEnergyLogId',
                  'duration', 'voltage', 'watts1', 'watts2', 'watts3', 'wattsTotal']
