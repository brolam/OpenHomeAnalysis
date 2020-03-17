from django.contrib import admin
from django.conf.urls import url
from django.urls import path, include, re_path
from rest_framework import routers
from rest_framework_jwt.views import obtain_jwt_token
from django.views.generic import TemplateView
from .views import UserViewSet, SensorViewSet, CostViewSet, SensorLogBatchViewSet

router = routers.DefaultRouter()
router.register(r'user', UserViewSet)
router.register(r'sensor', SensorViewSet)
router.register(r'cost', CostViewSet)
router.register(r'sensorLogBatch', SensorLogBatchViewSet)

urlpatterns = [
    path('', TemplateView.as_view(
        template_name='front-end/index.html'), name='home'),
    re_path(r'^app/*', TemplateView.as_view(
        template_name='front-end/index.html'), name='home'),
    path('token-auth/', obtain_jwt_token, name='token-auth'),
    path('admin/', admin.site.urls),
    path('api/', include(router.urls))
]
