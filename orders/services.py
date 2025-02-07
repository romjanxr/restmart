from django.db import transaction
from orders.models import Order, OrderItem, CartItem, Cart
from rest_framework.exceptions import ValidationError, PermissionDenied

class OrderService:
    @staticmethod
    def create_order(user_id, cart_id):
        """
        This Service Creates order from cart id then delete the cart
        """
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
                    quantity = item.quantity,
                    total_price = item.product.price * item.quantity
                )
                for item in cart_items
            ]

            OrderItem.objects.bulk_create(order_items)
            cart.delete() # delete cart after deleting order
            return order

    @staticmethod
    def cancel_order(order, user):
        """
        Handles order cancellation.
        - Customers can cancel their own orders if they are not delivered.
        - Admins can cancel any order.
        """

        if user.is_staff:
            order.status = Order.CANCELED
            order.save()
            return order

        if order.user != user:
            raise PermissionDenied({'detail': 'You can only cancel your own order'})

        if order.status == Order.DELIVERED:
            raise ValidationError({'detail': 'You cannot cancel an order that has been delivered'})

        order.status = Order.CANCELED
        order.save()
        return order
