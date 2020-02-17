from django.contrib import admin
from .models import Sensor, DimTime, EnergyBill,  EnergyLog

admin.site.register(Sensor)
admin.site.register(DimTime)
admin.site.register(EnergyBill)
admin.site.register(EnergyLog)
