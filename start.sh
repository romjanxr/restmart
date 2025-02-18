#!/bin/bash

echo "Installing dependencies with pip..."
pip3 install -r requirements.txt

echo "Upgrading pip..."
python3 -m pip install --upgrade pip

echo "Applying database migrations..."
python3 manage.py migrate --noinput

echo "Collecting static files..."
python3 manage.py collectstatic --noinput

echo "Starting Uvicorn ASGI server..."
python manage.py runserver 0.0.0.0:${PORT:-8000}