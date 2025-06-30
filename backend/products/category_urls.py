from django.urls import path
from products import views

urlpatterns = [
    path('', views.view_categories, name='category-list'),
    path('<int:id>/', views.view_specific_category, name='category-detail'),
]