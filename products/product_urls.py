from django.urls import path
from products import views

urlpatterns = [
    path('', views.view_products, name='product-list'),
    path('<int:id>/', views.view_specific_product, name='product-detail'),
]