from django.contrib.auth.models import User
from .models import Sensor, EnergyLog, EnergyLog, SensorLogBatch, DimTime
from django.db.models import Sum
from rest_framework import viewsets, serializers, status, permissions
from rest_framework.response import Response
from rest_framework.decorators import action
from django.http import HttpResponse
from django.views import View
from .serializers import UserSerializer, SensorListSerializer, SensorSerializer, EnergyLogSerializer, SeriesDaySerializer, SeriesHourSerializer, SensorLogBatchSerializer
import csv
from datetime import timedelta, datetime


class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects
    serializer_class = UserSerializer

    def get_queryset(self):
        user = self.request.user
        return User.objects.filter(id=user.id)


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

    @action(detail=True,  methods=['get'])
    def between_unix_time_logs(self, request, pk):
        start = request.query_params.get('start')
        end = request.query_params.get('end')
        sensor = self.get_queryset().get(pk=pk)
        energy_logs = sensor.get_between_unix_time_logs(start, end)
        serializer = EnergyLogSerializer(energy_logs, many=True)
        return Response(serializer.data)

    @action(detail=True,  methods=['get'])
    def series_per_day(self, request, pk):
        year = request.query_params.get('year')
        month = request.query_params.get('month')
        sensor = self.get_queryset().get(pk=pk)
        duration = Sum('energylog__duration')
        kwh1 = (duration * Sum('energylog__watts1') / 3600) / 1000
        kwh2 = (duration * Sum('energylog__watts2') / 3600) / 1000
        kwh3 = (duration * Sum('energylog__watts3') / 3600) / 1000
        serie_by_day = DimTime.objects.filter(
            sensor=sensor, year=year, month=month).values('day').annotate(duration=duration,  kwh1=kwh1, kwh2=kwh2, kwh3=kwh3, total=kwh1 + kwh2 + kwh3)
        serializer = SeriesSerializer(serie_by_day, many=True)
        return Response(serializer.data)

    @action(detail=True,  methods=['get'])
    def series_per_hour(self, request, pk):
        date_int = request.query_params.get('date')
        start_date = datetime.strptime(date_int, '%Y%m%d')
        end_date = start_date + timedelta(hours=23, minutes=59, seconds=59)
        print('start_date', start_date)
        print('end_date', end_date)
        sensor = Sensor.objects.get(pk=pk)
        duration = Sum('energylog__duration')
        wh1 = (duration * Sum('energylog__watts1') / 3600)
        wh2 = (duration * Sum('energylog__watts2') / 3600)
        wh3 = (duration * Sum('energylog__watts3') / 3600)
        serie_by_hour = DimTime.objects.filter(
            sensor=sensor, date_time__range=(start_date, end_date)).values('hour').annotate(duration=duration,  wh1=wh1, wh2=wh2, wh3=wh3, total=wh1 + wh2 + wh3)
        serializer = SeriesHourSerializer(serie_by_hour, many=True)
        return Response(serializer.data)

    @action(detail=True, methods=['get'])
    def download_csv_logs(self, request, pk):
        start = request.query_params.get('start')
        end = request.query_params.get('end')
        sensor = self.get_queryset().get(pk=pk)
        energy_logs = sensor.get_between_unix_time_logs(start, end)
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


class EnergyLogCSVviewSet(View):

    def get(self, request, sensor_id):
        #print( request.GET['_sensor_id'] )
        # The only line to customize
        model_class = EnergyLog
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
