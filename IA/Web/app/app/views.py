from django.contrib.auth.models import User
from .models import OhaSensor, OhaEnergyLog, OhaEnergyLog, OhaSensorLogBatch
from rest_framework import viewsets, serializers, status, permissions, authentication
from rest_framework.response import Response
from .serializers import UserSerializer, OhaSensorSerializer, OhaEnergyLogSerializer, OhaSensorLogBatchSerializer


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all().order_by('-date_joined')
    serializer_class = UserSerializer


class OhaSensorViewSet(viewsets.ModelViewSet):
    queryset = OhaSensor.objects.all()
    serializer_class = OhaSensorSerializer


class OhaSensorLogBatchViewSet(viewsets.ModelViewSet):
    class SensorPermissions(permissions.BasePermission):
        def has_permission(self, request, view):
            return True

    class SensorAuthentication(authentication.BaseAuthentication):
        def authenticate(self, request):
            user = User.objects.get(username="root")
            return (user, None)

    queryset = OhaSensorLogBatch.objects
    serializer_class = OhaSensorLogBatchSerializer
    permission_classes = (SensorPermissions,)
    authentication_classes = [SensorAuthentication]

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        return Response(status=status.HTTP_201_CREATED)


class OhaEnergyLogViewSet(viewsets.ModelViewSet):
    queryset = OhaEnergyLog.objects.all().order_by('-id')[1:10]
    serializer_class = OhaEnergyLogSerializer
