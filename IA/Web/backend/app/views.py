from django.contrib.auth.models import User
from .models import OhaSensor, OhaEnergyLog, OhaEnergyLog, OhaSensorLogBatch
from rest_framework import viewsets, serializers, status, permissions
from rest_framework.response import Response
from rest_framework.decorators import action
from django.http import HttpResponse
from django.views import View
from .serializers import UserSerializer, OhaSensorListSerializer, OhaSensorSerializer, OhaEnergyLogSerializer, OhaSensorLogBatchSerializer
import csv

class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects
    serializer_class = UserSerializer

    def get_queryset(self):
        user = self.request.user
        return User.objects.filter(id=user.id)

class OhaSensorViewSet(viewsets.ModelViewSet):
    queryset = OhaSensor.objects
    serializer_class = OhaSensorSerializer

    def get_queryset(self):
        user = self.request.user
        return OhaSensor.objects.filter(owner=user)

    @action(detail=False)
    def simple_list(self, request):
        sensors = self.get_queryset()
        print(sensors)
        serializer = OhaSensorListSerializer(sensors, many=True)
        return Response(serializer.data)

    @action(detail=True)
    def recent_log(self, request, pk=None):
        sensor = OhaSensor.objects.get(pk=pk)
        serializer = OhaEnergyLogSerializer(sensor.get_recent_log())
        return Response(serializer.data)

    @action(detail=True,  methods=['get'])
    def search_logs(self, request, pk):
        start_unix_time = request.query_params.get('start_unix_time')
        end_unix_time = request.query_params.get('end_unix_time')
        sensor = OhaSensor.objects.get(pk=pk)
        energy_logs = OhaEnergyLog.objects.filter(oha_sensor=sensor)
        serializer = OhaEnergyLogSerializer(energy_logs, many=True)
        return Response(serializer.data)


class OhaSensorLastLogViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = OhaEnergyLog.objects
    serializer_class = OhaEnergyLogSerializer

    def get_queryset(self):
        sensor = self.request.sensor
        return sensor.get_last_log()

class OhaSensorListViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = OhaSensor.objects
    serializer_class = OhaSensorListSerializer

    def get_queryset(self):
        user = self.request.user
        return OhaSensor.objects.filter(owner=user)

class OhaSensorLogBatchViewSet(viewsets.ModelViewSet):
    class SensorPermissions(permissions.BasePermission):
        def has_permission(self, request, view):
            return True
    queryset = OhaSensorLogBatch.objects
    serializer_class = OhaSensorLogBatchSerializer
    permission_classes = (SensorPermissions,)

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        return Response(status=status.HTTP_201_CREATED)

class OhaEnergyLogCSVviewSet(View):

    def get(self, request, oha_sensor_id):
        print(request.user)
        #print( request.GET['oha_sensor_id'] )
        # The only line to customize
        model_class = OhaEnergyLog
        meta = model_class._meta
        field_names = ['id', 'unix_time', 'duration', 'voltage', 'watts1',
                       'watts2', 'watts3', 'watts_total', 'sensor_convection']
        response = HttpResponse(content_type='text/csv')
        response['Content-Disposition'] = 'attachment; filename={}.csv'.format(
            meta)
        writer = csv.writer(response)
        writer.writerow(field_names)
        for obj in model_class.objects.all():
            row = writer.writerow([getattr(obj, field)
                                   for field in field_names])
        return response
