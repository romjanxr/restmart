from django.contrib import admin
from django.urls import path, include
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from rest_framework import permissions
from django.conf.urls.static import static
from django.conf import settings
from api.views import api_root_view
if settings.DEBUG == True:
    from debug_toolbar.toolbar import debug_toolbar_urls

schema_view = get_schema_view(
    openapi.Info(
        title='RESTMart E-commerce API',
        default_version='v1',
        description='API docmentation for RESTMart E-commerce project',
        contact=openapi.Contact(email="romjanvr5@gmail.com"),
        license=openapi.License(name="BSD License"),
    ),
    public=True,
    permission_classes=(permissions.AllowAny,), # Allows all the users to views the doc
)

urlpatterns = [
    path('admin/', admin.site.urls),
    path("", api_root_view, name='api-root'),
    path('api/v1/', include('api.urls')),
    path('api-auth/', include('rest_framework.urls')),
    path('swagger/', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    path('redoc/', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc-ui'),
    path('swagger.json/', schema_view.without_ui(cache_timeout=0), name='schema-json' )
] 

if settings.DEBUG == True:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
    urlpatterns += [path('silk/', include('silk.urls', namespace='silk'))]
    urlpatterns += debug_toolbar_urls()
