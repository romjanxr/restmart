#!/bin/bash
echo "Installing Poetry..."
python3 -m pip install poetry

echo "Installing dependencies with Poetry..."
poetry install --no-root --no-interaction

echo "Applying database migrations..."
poetry run python3 manage.py migrate --noinput

echo "Collecting static files..."
poetry run python3 manage.py collectstatic --noinput

echo "Starting Daphne ASGI server..."
poetry run daphne -b 0.0.0.0 -p $PORT restmart.asgi:application
