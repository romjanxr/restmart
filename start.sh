#!/bin/bash
echo "Installing Poetry..."
python -m pip install poetry

echo "Installing dependencies with Poetry..."
poetry install --no-root --no-interaction

echo "Applying database migrations..."
poetry run python manage.py migrate --noinput

echo "Collecting static files..."
poetry run python manage.py collectstatic --noinput

echo "Starting Daphne ASGI server..."
poetry run daphne -b 0.0.0.0 -p $PORT restmart.asgi:application
