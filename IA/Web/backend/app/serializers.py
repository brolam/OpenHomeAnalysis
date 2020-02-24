from django.contrib.auth.models import User
from .models import Sensor, EnergyLog, Cost, SensorLogBatch
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


class CostSerializer(serializers.ModelSerializer):

    class Meta:
        model = Cost
        fields = '__all__'


class SensorListSerializer(serializers.ModelSerializer):
    class Meta:
        model = Sensor
        fields = ['id', 'name']


class EnergyLogSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    datetime = serializers.DateTimeField()
    duration = serializers.DecimalField(max_digits=20, decimal_places=2)
    phase1 = serializers.DecimalField(
        max_digits=20, decimal_places=2, source="watts1")
    phase2 = serializers.DecimalField(
        max_digits=20, decimal_places=2, source="watts2")
    phase3 = serializers.DecimalField(
        max_digits=20, decimal_places=2, source="watts3")
    total = serializers.DecimalField(
        max_digits=20, decimal_places=2, source="watts_total")


class SensorLogBatchSerializer(serializers.ModelSerializer):
    class Meta:
        model = SensorLogBatch
        fields = '__all__'


class SeriesEnergyLogSerializer(serializers.Serializer):
    x = serializers.IntegerField()
    y1 = serializers.DecimalField(max_digits=20, decimal_places=2)
    y2 = serializers.DecimalField(max_digits=20, decimal_places=2)
    y3 = serializers.DecimalField(max_digits=20, decimal_places=2)


class CostSummarySerializer(serializers.Serializer):
    title = serializers.CharField()
    cost_total = serializers.FloatField()
