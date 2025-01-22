from django.shortcuts import get_object_or_404
from django.http import HttpResponse
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from products.serializers import ProductSerializer
from products.models import Product


# Create your views here.
@api_view(['GET', 'POST', 'DELETE'])
def view_products(request):
    queryset = Product.objects.all()
    serializer = ProductSerializer(queryset, many=True, context={'request': request})
    return Response(serializer.data)

@api_view()
def view_specific_product(request, id):
    product = get_object_or_404(Product,pk=id)
    serializer = ProductSerializer(product)
    return Response(serializer.data)

@api_view()
def collection_detail(request, pk):
    return Response('ok')