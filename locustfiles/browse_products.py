from locust import HttpUser, task, between
from random import randint

class WebsiteUser(HttpUser):
    wait_time = between(1, 5)

    @task(2)
    def view_products(self):
        print('Views Products')
        category_id = randint(1, 4)
        self.client.get(f'/api/v1/products/?category_id={category_id}', name='/api/v1/products')

    @task(4)
    def view_product(self):
        print('View Product')
        product_id = randint(1, 34)
        self.client.get(f'/api/v1/products/{product_id}', name='/api/v1/products/:id')

    @task(1)
    def add_to_cart(self):
        print('Add to cart')
        product_id = randint(1, 10)
        self.client.post(
            f'/api/v1/carts/{self.cart_id}/items/',
            name='/api/v1/carts/items',
            json={'product_id': product_id, 'quantity': 1}
        )

    def on_start(self):
        print('on start')

        # Step 1: Authenticate and get JWT token
        auth_response = self.client.post('/api/v1/auth/jwt/create/', json={
            "email": "test2@gmail.com",
            "password": "TestUser"
        })

        print(f"Auth response: {auth_response.status_code} - {auth_response.text}")

        if auth_response.status_code == 200:
            token = auth_response.json().get('access')
            self.client.headers.update({"Authorization": f"Bearer {token}"})  # Use "Bearer" instead of "JWT"
            print("Authenticated successfully")

            # Get the user ID from the authentication response
            user_id = auth_response.json().get('user_id')
            print(f"Authenticated user_id: {user_id}")

            # Step 2: Create a cart (Only authenticated users can do this)
            cart_response = self.client.post('/api/v1/carts/', json={'user': user_id})
            print(f"Cart response: {cart_response.status_code} - {cart_response.text}")

            if cart_response.status_code == 201:
                self.cart_id = cart_response.json().get('id')
                print(f'Cart created: {self.cart_id}')
            else:
                print(f'Failed to create cart: {cart_response.status_code} - {cart_response.text}')
                self.cart_id = None
        else:
            print(f'Authentication failed: {auth_response.status_code} - {auth_response.text}')
            self.cart_id = None
