from django.shortcuts import get_object_or_404
from django.http import HttpResponse
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from products.serializers import ProductSerializer, CategorySerializer
from products.models import Product, Category


# Create your views here.
@api_view(['GET', 'POST'])
def view_products(request):
    if request.method == 'GET':
        queryset = Product.objects.all()
        serializer = ProductSerializer(queryset, many=True, context={'request': request})
        return Response(serializer.data)
    if request.method == 'POST':
        serializer = ProductSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        print(serializer.validated_data)
        return Response('okay')
            

@api_view()
def view_specific_product(request, id):
    product = get_object_or_404(Product,pk=id)
    serializer = ProductSerializer(product)
    return Response(serializer.data)

@api_view()
def view_categories(request):
    categories = Category.objects.all()
    serializer = CategorySerializer(categories, many=True)
    return Response(serializer.data)

@api_view()
def view_specific_category(request, id):
    category = get_object_or_404(Category,pk=id)
    serializer = CategorySerializer(category)
    return Response(serializer.data)
