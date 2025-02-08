from locust import HttpUser, task, between
from random import randint

class WebsiteUser(HttpUser):
    wait_time = between(1, 5)
    @task(2)
    def view_products(self): 
        category_id = randint(1,4) 
        self.client.get(f'/api/v1/products/?category_id={category_id}', name='/api/v1/products')

    @task(4)
    def view_product(self):
        product_id = randint(1, 34)
        self.client.get(f'/api/v1/products/{product_id}', name='/api/v1/products/:id')

    @task(1)
    def add_to_cart(self):
        product_id = randint(1, 10)
        self.client.post(
            f'/api/v1/carts/{self.cart_id}/items/',
            name='/api/v1/carts/items',
            json={'product_id': product_id, 'quantity': 1}
        )

    def on_start(self):
        response = self.client.post('/api/v1/carts/')
        result = response.json()
        self.cart_id = result['id']
