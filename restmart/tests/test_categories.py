from rest_framework.test import APIClient
from rest_framework import status
import pytest

@pytest.mark.django_db
class TestCreateCategories:
    # @pytest.mark.skip # To skip a test temporary
    def test_if_user_anonymous_returns_401(self):
        client = APIClient()
        response = client.post('/api/v1/categories/', {'name': 'a'})
        assert response.status_code == status.HTTP_401_UNAUTHORIZED

