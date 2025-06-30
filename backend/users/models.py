from django.contrib.auth.models import AbstractUser
from django.db import models
from users.managers import CustomUserManager

class User(AbstractUser):
    username = None
    email = models.EmailField(unique=True)
    address = models.TextField(blank=True, null=True)
    phone_number = models.CharField(max_length=15, blank=True, null=True)

    USERNAME_FIELD = 'email'  # Use email instead of username for authentication
    REQUIRED_FIELDS = []  # Removes the default 'username' field from the required list

    objects = CustomUserManager()

    def __str__(self):
        return self.email
