from .base import *

DEBUG = True

INSTALLED_APPS.insert(0, "whitenoise.runserver_nostatic",)
INSTALLED_APPS += [
    "debug_toolbar",
    "silk"
]

INTERNAL_IPS = [
    # ...
    "127.0.0.1",
    # ...
]

MIDDLEWARE.insert(0,"debug_toolbar.middleware.DebugToolbarMiddleware")
MIDDLEWARE += ['silk.middleware.SilkyMiddleware',]

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': BASE_DIR / 'db.sqlite3',
    }
}
