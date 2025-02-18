from django.shortcuts import render

# Create your views here.

def api_root_view(request):
    return redirect('schema-swagger-ui')