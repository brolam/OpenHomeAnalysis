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
        fields = '__all__'


class OhaEnergyLogSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaEnergyLog
        fields = '__all__'


class OhaEnergyLogBatchSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaEnergyLogBatch
        fields = '__all__'
