from products.serializers import ProductSerializer, CategorySerializer, CategorySerializer, ReviewSerializer, ProductImageSerializer
from products.models import Product, Category, Review, ProductImage
from products.filters import ProductFilter
from products.paginations import DefaultPagination
from api.permissions import IsAdminOrReadOnly
from products.permissions import IsReviewAuthorOrReadOnly
from drf_yasg.utils import swagger_auto_schema
from django.db.models import Count
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import SearchFilter, OrderingFilter
from rest_framework.permissions import DjangoModelPermissions

class ProductViewSet(ModelViewSet):
    """This view provide options for 
        - Creating Product
        - Updating Product
        - Deleting Product
        - And Getting Product Instance
    """
    queryset = Product.objects.prefetch_related('images').all()
    serializer_class = ProductSerializer
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_class = ProductFilter
    pagination_class = DefaultPagination
    search_fields = ['name', 'description', 'category__name']
    ordering_fields = ['price', 'updated_at']
    # permission_classes = [IsAdminOrReadOnly]
    # permission_classes = [DjangoModelPermissions] # model onujayi access dibe

    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        if instance.stock > 10:
            return Response({'message': 'Product has stock more than 10 could not be deleted'})
        self.perform_destroy(instance)
        return Response(status=status.HTTP_204_NO_CONTENT)

class ProductImageViewSet(ModelViewSet):
    serializer_class = ProductImageSerializer

    def get_queryset(self):
        return ProductImage.objects.filter(product_id=self.kwargs['product_pk'])

    def perform_create(self, serializer):
        serializer.save(product_id=self.kwargs['product_pk'])

class CategoryViewSet(ModelViewSet):
    queryset = Category.objects.annotate(products_count=Count('products')).all()
    serializer_class = CategorySerializer
    permission_classes = [IsAdminOrReadOnly]

    
       
class ReviewViewSet(ModelViewSet):
    serializer_class = ReviewSerializer
    permission_classes = [IsReviewAuthorOrReadOnly]

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    def get_queryset(self):
        return Review.objects.filter(product_id=self.kwargs['product_pk'])

    def get_serializer_context(self):
        return {'product_id': self.kwargs['product_pk']}