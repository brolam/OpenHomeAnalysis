from django.contrib.auth.models import User
from .models import OhaSensor, OhaEnergyLog, OhaSensorLogBatch
from rest_framework import serializers


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['url', 'username', 'email', 'groups']


class OhaSensorSerializer(serializers.ModelSerializer):
    owner = serializers.HiddenField(default=serializers.CurrentUserDefault())
    class Meta:
        model = OhaSensor
        fields = '__all__'

class OhaSensorListSerializer(serializers.ModelSerializer):
    class Meta:
        model = OhaSensor
        fields = ['id', 'name']

class OhaEnergyLogSerializer(serializers.ModelSerializer):
    class Meta:
        model = OhaEnergyLog
        fields = '__all__'


class OhaSensorLogBatchSerializer(serializers.ModelSerializer):
    class Meta:
        model = OhaSensorLogBatch
        fields = '__all__'
