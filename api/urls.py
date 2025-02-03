from django.urls import include,path
from rest_framework_nested import routers
from products import views as productViews
from orders import views as orderViews

router = routers.DefaultRouter()
router.register('products', productViews.ProductViewSet, basename='product')
router.register('categories', productViews.CategoryViewSet)
router.register('carts', orderViews.CartViewset, basename='cart')
router.register('orders', orderViews.OrderViewset, basename='order')

products_router = routers.NestedDefaultRouter(router, 'products', lookup='product')
products_router.register('reviews', productViews.ReviewViewSet, basename='product-reviews')

carts_router = routers.NestedDefaultRouter(router, 'carts', lookup='cart')
carts_router.register('items', orderViews.CartItemViewSet, basename='cart-items')

urlpatterns = [
    path('', include(router.urls)),
    path('', include(products_router.urls)),
    path('', include(carts_router.urls)),
    path('auth/', include('djoser.urls')),
    path('auth/', include('djoser.urls.jwt')),
] 