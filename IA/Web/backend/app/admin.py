from django.contrib import admin
from .models import OhaSensor, OhaEnergyLog

admin.site.register(OhaSensor)
admin.site.register(OhaEnergyLog)
