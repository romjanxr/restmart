#!/bin/bash

echo "Installing dependencies with pip..."
pip3 install -r requirements.txt

echo "Applying database migrations..."
python3 manage.py migrate --noinput

echo "Collecting static files..."
python3 manage.py collectstatic --noinput

echo "Starting Daphne ASGI server..."
daphne -b 0.0.0.0 -p $PORT restmart.asgi:application
