from django.urls import include,path

urlpatterns = [
    path('products/', include('products.product_urls')),
    path('categories/', include('products.category_urls')),
]