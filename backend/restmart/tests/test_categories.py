from rest_framework import status
from products.models import Category
from model_bakery import baker
import pytest

@pytest.fixture
def create_categories(api_client):
    def do_create_categories(collection):
        return api_client.post('/api/v1/categories/', collection)
    return do_create_categories

@pytest.mark.django_db
class TestCreateCategories:
    # @pytest.mark.skip # To skip a test temporary
    def test_if_user_anonymous_returns_401(self, create_categories):
        response = create_categories({'name': 'a'})

        assert response.status_code == status.HTTP_401_UNAUTHORIZED

    def test_if_user_is_not_admin_returns_403(self, authenticate, create_categories):
        authenticate()

        response = create_categories({'name': 'a'})

        assert response.status_code == status.HTTP_403_FORBIDDEN

    def test_if_data_is_invalid_returns_400(self, authenticate, create_categories):
        authenticate(is_staff=True)

        response = create_categories({'name': ''})

        assert response.status_code == status.HTTP_400_BAD_REQUEST
        assert response.data['name'] is not None

    def test_if_data_is_valid_returns_201(self, authenticate, create_categories):
        authenticate(is_staff=True)

        response = create_categories({'name': 'a'})

        assert response.status_code == status.HTTP_201_CREATED
        assert response.data['id'] > 0

@pytest.mark.django_db
class TestRetriveCategories:
    def test_if_category_exists_return_200(self, api_client):
        category = baker.make(Category)
        response = api_client.get(f'/api/v1/categories/{category.id}/')
        assert response.status_code == status.HTTP_200_OK
        assert response.data == {
            'id': category.id,
            'name': category.name,
            'description': category.description,
            'products_count': 0
        }
