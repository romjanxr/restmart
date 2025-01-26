from django.urls import include,path
from products import views
from rest_framework_nested import routers
# from pprint import pprint

router = routers.DefaultRouter()
router.register('products', views.ProductViewSet)
router.register('categories', views.CategoryViewSet)


product_routers = routers.NestedDefaultRouter(router, 'products', lookup='product')
product_routers.register('reviews', views.ReviewViewSet, basename='product-reviews')

urlpatterns = router.urls

urlpatterns = [
    path('', include(router.urls)),
    path('', include(product_routers.urls))
] 

# pprint(router.urls + product_routers.urls)