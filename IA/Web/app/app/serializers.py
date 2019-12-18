from django.contrib.auth.models import User
from .models import OhaSensor, OhaEnergyLog, OhaSensorLogBatch
from rest_framework import serializers


class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ['url', 'username', 'email', 'groups']


class OhaSensorSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaSensor
        fields = '__all__'


class OhaEnergyLogSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaEnergyLog
        fields = '__all__'


class OhaSensorLogBatchSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OhaSensorLogBatch
        fields = '__all__'
