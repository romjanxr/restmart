from rest_framework import serializers
from decimal import Decimal
from products.models import Product, Category

class CategorySerializer(serializers.Serializer):
    id = serializers.IntegerField()
    name = serializers.CharField()

# class ProductSerializer(serializers.Serializer):
#     id = serializers.IntegerField()
#     name = serializers.CharField()
#     unit_price = serializers.DecimalField(max_digits=10, decimal_places=2, source='price')
#     price_with_tax = serializers.SerializerMethodField(method_name='calculate_tax')
#     # category = serializers.PrimaryKeyRelatedField(
#     #     queryset = Category.objects.all()
#     # )
#     # category = serializers.StringRelatedField()
#     # category = CategorySerializer()
#     category = serializers.HyperlinkedRelatedField(
#         queryset = Category.objects.all(),
#         view_name='collection-detail'
#     )
    
#     def calculate_tax(self, product: Product):
#         return round(product.price * Decimal(1.1), 2)
