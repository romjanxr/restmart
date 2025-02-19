from django.core.management.base import BaseCommand
from django.core.management import call_command
from django.contrib.auth import get_user_model
User = get_user_model()

class Command(BaseCommand):
    help = 'Resets the database and loads initial data'

    def handle(self, *args, **kwargs):
        # Flush the database
        call_command('flush', '--noinput')

        # Load initial data (if you have a fixture)
        call_command('loaddata', 'fixtures/product_data.json')

        # Recreate demo users
        User.objects.create_superuser('admin@example.com', 'DemoAdmin123')
        User.objects.create_user('test@example.com', 'DemoUser123')

        self.stdout.write(self.style.SUCCESS('Database reset successfully.'))
