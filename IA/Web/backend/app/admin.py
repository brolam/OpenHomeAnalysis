from django.contrib import admin
from .models import Sensor, DimTime, Cost,  EnergyLog, SensorLogBatch

admin.site.register(Sensor)
admin.site.register(DimTime)
admin.site.register(Cost)
admin.site.register(SensorLogBatch)
admin.site.register(EnergyLog)
