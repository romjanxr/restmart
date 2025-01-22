from django.shortcuts import get_object_or_404
from django.http import HttpResponse
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from products.serializers import ProductSerializer, CategorySerializer
from products.models import Product, Category
from drf_yasg.utils import swagger_auto_schema
from django.db.models import Count

@swagger_auto_schema(
    method='post',
    operation_description="Create a new product",
    request_body=ProductSerializer,  # Request body schema for POST
    responses={
        201: "Product created successfully",
        400: "Validation error occurred",
    }
)
@api_view(['GET', 'POST'])
def view_products(request):
    if request.method == 'GET':
        queryset = Product.objects.all()
        serializer = ProductSerializer(queryset, many=True, context={'request': request})
        return Response(serializer.data)
    if request.method == 'POST':
        serializer = ProductSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
            

@swagger_auto_schema(
    method='put',
    operation_description="Create a new product",
    request_body=ProductSerializer,  # Request body schema for PUT
    responses={
        200: "Product updated successfully",
        400: "Validation error occurred",
    }
)
@api_view(['GET', 'PUT', 'DELETE'])
def view_specific_product(request, id):
    product = get_object_or_404(Product,pk=id)
    if request.method == 'GET':
        serializer = ProductSerializer(product)
        return Response(serializer.data)
    elif request.method == 'PUT':
        serializer = ProductSerializer(product, data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data)
    elif request.method == 'DELETE':
        product.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

@api_view(['GET', 'POST'])
def view_categories(request):
    if request.method == 'GET':
        categories = Category.objects.annotate(products_count=Count('products')).all()
        serializer = CategorySerializer(categories, many=True)
        return Response(serializer.data)
    elif request.method == 'POST':
        serializer = CategorySerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)

@api_view(['GET', 'PUT', 'DELETE'])
def view_specific_category(request, id):
    category = get_object_or_404(
        Category.objects.annotate(products_count=Count('products')).all(),
        pk=id
    )
    if request.method == 'GET':
        serializer = CategorySerializer(category)
        return Response(serializer.data)
    elif request.method == 'PUT':
        serializer = CategorySerializer(category, data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data)
    elif request.method == 'DELETE':
        category.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
