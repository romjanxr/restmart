from django.shortcuts import render
from django.shortcuts import redirect

# Create your views here.

def api_root_view(request):
    return redirect('schema-swagger-ui')