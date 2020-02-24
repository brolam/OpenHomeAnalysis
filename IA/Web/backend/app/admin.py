from django.contrib import admin
from .models import Sensor, DimTime, Cost,  EnergyLog

admin.site.register(Sensor)
admin.site.register(DimTime)
admin.site.register(Cost)
admin.site.register(EnergyLog)
