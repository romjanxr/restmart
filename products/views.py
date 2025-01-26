from products.serializers import ProductSerializer, CategorySerializer, CategorySerializer, ReviewSerializer
from products.models import Product, Category, Review
from drf_yasg.utils import swagger_auto_schema
from django.db.models import Count
from rest_framework.viewsets import ModelViewSet
from django_filters.rest_framework import DjangoFilterBackend
from products.filters import ProductFilter
from rest_framework.filters import SearchFilter, OrderingFilter

class ProductViewSet(ModelViewSet):
    queryset = Product.objects.all()
    serializer_class = ProductSerializer
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_class = ProductFilter
    search_fields = ['name', 'description', 'category__name']
    ordering_fields = ['price', 'updated_at']

    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        if instance.stock > 10:
            return Response({'message': 'Product has stock more than 10 could not be deleted'})
        self.perform_destroy(instance)
        return Response(status=status.HTTP_204_NO_CONTENT)

class CategoryViewSet(ModelViewSet):
    queryset = Category.objects.annotate(products_count=Count('products')).all()
    serializer_class = CategorySerializer
       
class ReviewViewSet(ModelViewSet):
    serializer_class = ReviewSerializer

    def get_queryset(self):
        return Review.objects.filter(product_id=self.kwargs['product_pk'])

    def get_serializer_context(self):
        return {'product_id': self.kwargs['product_pk']}