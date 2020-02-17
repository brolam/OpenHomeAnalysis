from django.contrib.auth.models import User
from .models import Sensor, EnergyLog, SensorLogBatch
from rest_framework import serializers


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['url', 'username', 'email', 'groups']


class SensorSerializer(serializers.ModelSerializer):
    owner = serializers.HiddenField(default=serializers.CurrentUserDefault())

    class Meta:
        model = Sensor
        fields = '__all__'


class SensorListSerializer(serializers.ModelSerializer):
    class Meta:
        model = Sensor
        fields = ['id', 'name']


class EnergyLogSerializer(serializers.ModelSerializer):
    class Meta:
        model = EnergyLog
        fields = '__all__'


class SensorLogBatchSerializer(serializers.ModelSerializer):
    class Meta:
        model = SensorLogBatch
        fields = '__all__'


class SeriesDaySerializer(serializers.Serializer):
    day = serializers.IntegerField()
    duration = serializers.DecimalField(max_digits=20, decimal_places=2)
    kwh1 = serializers.DecimalField(max_digits=20, decimal_places=2)
    kwh2 = serializers.DecimalField(max_digits=20, decimal_places=2)
    kwh3 = serializers.DecimalField(max_digits=20, decimal_places=2)
    total = serializers.DecimalField(max_digits=20, decimal_places=2)


class SeriesHourSerializer(serializers.Serializer):
    hour = serializers.IntegerField()
    duration = serializers.DecimalField(max_digits=20, decimal_places=2)
    wh1 = serializers.DecimalField(max_digits=20, decimal_places=2)
    wh2 = serializers.DecimalField(max_digits=20, decimal_places=2)
    wh3 = serializers.DecimalField(max_digits=20, decimal_places=2)
    total = serializers.DecimalField(max_digits=20, decimal_places=2)
