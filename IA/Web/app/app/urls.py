from django.contrib import admin
from django.urls import path, include
from rest_framework import routers
from rest_framework_jwt.views import obtain_jwt_token
from django.views.generic import TemplateView
from .views import UserViewSet, OhaSensorViewSet, OhaEnergyLogViewSet, OhaSensorLogBatchViewSet

router = routers.DefaultRouter()
router.register(r'user', UserViewSet)
router.register(r'sensor', OhaSensorViewSet)
router.register(r'sensorLogBatch', OhaSensorLogBatchViewSet)
router.register(r'energyLog', OhaEnergyLogViewSet)


urlpatterns = [
    path('', TemplateView.as_view(
        template_name='front-end/index.html'), name='home'),
    path('token-auth/', obtain_jwt_token, name='token-auth'),
    path('admin/', admin.site.urls),
    path('api/', include(router.urls)),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework')),
]
