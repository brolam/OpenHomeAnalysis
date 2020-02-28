from django.contrib.auth.models import User
from .models import Sensor, EnergyLog, EnergyLog, Cost, SensorLogBatch, DimTime
from django.db.models import Sum
from rest_framework import viewsets, serializers, status, permissions
from rest_framework.response import Response
from rest_framework.decorators import action, detail_route
from django.http import HttpResponse
from django.views import View
from .serializers import UserSerializer, SensorListSerializer, SensorSerializer, EnergyLogSerializer, CostSerializer, SeriesSerializer,  SummaryCostDaySerializer, SensorLogBatchSerializer
import csv
from datetime import timedelta, datetime


class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects
    serializer_class = UserSerializer

    def get_queryset(self):
        user = self.request.user
        return User.objects.filter(id=user.id)


class CostViewSet(viewsets.ModelViewSet):
    queryset = Cost.objects
    serializer_class = CostSerializer


class SensorViewSet(viewsets.ModelViewSet):
    queryset = Sensor.objects
    serializer_class = SensorSerializer

    def get_queryset(self):
        user = self.request.user
        return Sensor.objects.filter(owner=user)

    @action(detail=False)
    def simple_list(self, request):
        sensors = self.get_queryset()
        serializer = SensorListSerializer(sensors, many=True)
        return Response(serializer.data)

    @action(detail=True)
    def recent_logs(self, request, pk=None):
        sensor = self.get_queryset().get(pk=pk)
        amount = int(request.query_params.get('amount', 10))
        serializer = EnergyLogSerializer(
            sensor.get_recent_logs(amount), many=True)
        return Response(serializer.data)

    @action(detail=True, methods=['get'], url_path="summary_cost_day(?:/(?P<year>[0-9]+))?(?:/(?P<month>[0-9]+))?(?:/(?P<day>[0-9]+))?")
    def summary_cost_day(self, request, pk=None, year=None, month=None, day=None):
        sensor = self.get_queryset().get(pk=pk)
        summary_cost_day = sensor.get_summary_cost_day(year, month, day)
        print('summary_cost_day', summary_cost_day)
        serializer = SummaryCostDaySerializer(summary_cost_day)
        return Response(serializer.data)

    @action(detail=True,  methods=['get'], url_path="series_per_hour(?:/(?P<year>[0-9]+))?(?:/(?P<month>[0-9]+))?(?:/(?P<day>[0-9]+))?")
    def series_per_hour(self, request, pk=None, year=None, month=None, day=None):
        sensor = Sensor.objects.get(pk=pk)
        serie_by_hour = sensor.get_series_by_hour(year, month, day)
        serializer = SeriesSerializer(serie_by_hour, many=True)
        return Response(serializer.data)

    @action(detail=True, methods=['get'])
    def download_csv_logs(self, request, pk):
        year = request.query_params.get('year')
        month = request.query_params.get('month')
        sensor = self.get_queryset().get(pk=pk)
        energy_logs = sensor.get_logs(year, month)
        field_names = ['id', 'unix_time', 'duration', 'voltage', 'watts1',
                       'watts2', 'watts3', 'watts_total', 'sensor_convection']
        response = HttpResponse(content_type='text/csv')
        response['Content-Disposition'] = 'attachment; filename={}.csv'.format(
            'energyLogs')
        writer = csv.writer(response)
        writer.writerow(field_names)
        for obj in energy_logs:
            row = writer.writerow([getattr(obj, field)
                                   for field in field_names])
        return response


class SensorLastLogViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = EnergyLog.objects
    serializer_class = EnergyLogSerializer

    def get_queryset(self):
        sensor = self.request.sensor
        return sensor.get_last_log()


class SensorListViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Sensor.objects
    serializer_class = SensorListSerializer

    def get_queryset(self):
        user = self.request.user
        return Sensor.objects.filter(owner=user)


class SensorLogBatchViewSet(viewsets.ModelViewSet):
    class SensorPermissions(permissions.BasePermission):
        def has_permission(self, request, view):
            return True

    queryset = SensorLogBatch.objects
    serializer_class = SensorLogBatchSerializer
    permission_classes = (SensorPermissions,)

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        return Response(status=status.HTTP_201_CREATED)
