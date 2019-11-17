from django.contrib.auth.models import User
from app.models import OhaDevice, OhaEnergyLog
from rest_framework import viewsets, serializers
from app.serializers import UserSerializer, OhaDeviceSerializer, OhaEnergyLogSerializer


class UserViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    queryset = User.objects.all().order_by('-date_joined')
    serializer_class = UserSerializer


class OhaDeviceViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    queryset = OhaDevice.objects.all()
    serializer_class = OhaDeviceSerializer


class OhaEnergyLogViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows groups to be viewed or edited.
    """
    queryset = OhaEnergyLog.objects.all()
    serializer_class = OhaEnergyLogSerializer
