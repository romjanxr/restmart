from .base import *

DEBUG = False
STATICFILES_STORAGE = "whitenoise.storage.CompressedStaticFilesStorage"


# Let Vercel handle static files
# Vercel will automatically serve them from staticfiles/.
# INSTALLED_APPS.remove("django.contrib.staticfiles")

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql',
        'NAME': config('DB_NAME'),
        'USER': config('DB_USER'),
        'PASSWORD': config('DB_PASSWORD'),
        'HOST': config('DB_HOST'),
        'PORT': config('DB_PORT'),
    }
}

SECURE_BROWSER_XSS_FILTER = True
SECURE_CONTENT_TYPE_NOSNIFF = True
SESSION_COOKIE_SECURE = True
CSRF_COOKIE_SECURE = True