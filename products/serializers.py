from rest_framework import serializers
from decimal import Decimal
from products.models import Product, Category

class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Category
        fields = ['id', 'name', 'description']

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

class ProductSerializer(serializers.ModelSerializer):
    class Meta:
        model = Product
        fields = ['id', 'name', 'price', 'category', 'price_with_tax']
        # fields = '__all__' # Bad Practice
    """Process for adding unit_price and category hyperlink is similar to normal serializer"""
    
    price_with_tax = serializers.SerializerMethodField(method_name='calculate_tax')

    def calculate_tax(self, product: Product):
        return round(product.price * Decimal(1.1), 2)

    def validate_price(self, value):
        if value < 0:
            raise serializers.ValidationError('Price could not be negative')
        return value

    def validate(self, data):
        if data['password'] != data['confirm_password']:
            raise serializers.ValidationError("Password didn't mathc")
