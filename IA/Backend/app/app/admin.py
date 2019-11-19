from django.contrib import admin
from .models import OhaDevice, OhaEnergyLog

admin.site.register(OhaDevice)
admin.site.register(OhaEnergyLog)