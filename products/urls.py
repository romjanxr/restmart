from django.urls import path
from products import views

urlpatterns = [
    path('', views.view_products),
    path('<int:id>/', views.view_specific_product),
    path('category/<int:pk>', views.collection_detail, name='collection-detail' )
]