from django.contrib import admin
from django.urls import path, include
from products.views import view_products
from debug_toolbar.toolbar import debug_toolbar_urls


urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/', include('api.urls')),
    path('api-auth/', include('rest_framework.urls')),
] + debug_toolbar_urls()
