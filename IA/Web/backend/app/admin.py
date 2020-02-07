from django.contrib import admin
from .models import OhaSensor, OhaSensorDimDate, OhaEnergyBill,  OhaEnergyLog

admin.site.register(OhaSensor)
admin.site.register(OhaSensorDimDate)
admin.site.register(OhaEnergyBill)
admin.site.register(OhaEnergyLog)
