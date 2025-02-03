from django.db import transaction
from orders.models import Order, OrderItem, CartItem, Cart

class OrderService:
    """
        This Service Creates order from cart id then delete the cart
    """
    @staticmethod
    def create_order(user_id, cart_id):
        with transaction.atomic():
            cart = Cart.objects.get(pk=cart_id) # Get the cart instance
            cart_items = cart.items.select_related('product').all()

            if not cart_items.exists():
                raise ValueError('Cart is empty')

            total_price = sum(item.product.price * item.quantity for item in cart_items)
            order = Order.objects.create(user_id=user_id, total_price=total_price)

            order_items = [
                OrderItem(
                    order = order,
                    product = item.product,
                    price = item.product.price,
                    quantity = item.quantity
                )
                for item in cart_items
            ]

            OrderItem.objects.bulk_create(order_items)
            cart.delete() # delete cart after deleting order
            return order