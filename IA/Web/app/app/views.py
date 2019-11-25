from django.contrib.auth.models import User
from .models import OhaDevice, OhaEnergyLog, OhaEnergyLog, OhaEnergyLogBatch
from rest_framework import viewsets, serializers
from .serializers import UserSerializer, OhaDeviceSerializer, OhaEnergyLogSerializer, OhaEnergyLogBatchSerializer


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all().order_by('-date_joined')
    serializer_class = UserSerializer


class OhaDeviceViewSet(viewsets.ModelViewSet):
    queryset = OhaDevice.objects.all()
    serializer_class = OhaDeviceSerializer


class OhaEnergyLogViewSet(viewsets.ModelViewSet):
    queryset = OhaEnergyLog.objects.all()
    serializer_class = OhaEnergyLogSerializer

class OhaEnergyLogBatchViewSet(viewsets.ModelViewSet):
    queryset = OhaEnergyLogBatch.objects.all()
    serializer_class = OhaEnergyLogBatchSerializer