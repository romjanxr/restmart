# DRF RESTMart E-Commerce API

This is a RESTful API built using Django Rest Framework (DRF) for an e-commerce system. The API provides functionalities for managing products, categories, carts, and orders, along with JWT authentication for user authentication and authorization.

## Features

- **Authentication & Authorization:** JWT-based authentication using Djoser and Simple JWT.
- **Product Management:** CRUD operations for products.
- **Category Management:** CRUD operations for product categories.
- **Cart System:** Add, remove, and update items in the cart.
- **Order Processing:** Create and manage orders.
- **Swagger Documentation:** Interactive API documentation using Swagger.
- **Secure Deployment:** Optimized settings for security and scalability.

## Technologies Used

- **Django** - High-level Python web framework
- **Django Rest Framework (DRF)** - Toolkit for building Web APIs
- **Djoser & Simple JWT** - Authentication and token management
- **PostgreSQL** - Database management system
- **Swagger** - API documentation
- **Whitenoise** - Static file serving
- **Cloudinary** - Media file storage (if applicable)

## Installation

### Prerequisites
Ensure you have the following installed:
- Python (>= 3.8)
- PostgreSQL
- Virtualenv (optional but recommended)
- Docker (if deploying with containers)

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/your-repository.git
   cd your-repository
   ```

2. **Create and Activate a Virtual Environment**
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows use `venv\Scripts\activate`
   ```

3. **Install Dependencies**
   ```bash
   pip install -r requirements.txt
   ```

4. **Configure Environment Variables**
   Create a `.env` file in the root directory and add the required environment variables:
   ```env
   SECRET_KEY=your-secret-key
   DEBUG=True
   DATABASE_URL=postgres://user:password@localhost:5432/db_name
   ```

5. **Apply Migrations and Create Superuser**
   ```bash
   python manage.py migrate
   python manage.py createsuperuser
   ```

6. **Run the Development Server**
   ```bash
   python manage.py runserver
   ```

7. **Access API Documentation**
   - Swagger UI: `http://127.0.0.1:8000/swagger/`

## API Endpoints

| Method | Endpoint                | Description                |
|--------|-------------------------|----------------------------|
| POST   | `/api/auth/register/`   | Register a new user        |
| POST   | `/api/auth/login/`      | Login and get JWT tokens   |
| GET    | `/api/products/`        | Get all products           |
| GET    | `/api/products/{id}/`   | Get product details        |
| POST   | `/api/cart/add/`        | Add product to cart        |
| POST   | `/api/orders/`          | Create an order            |


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
This project is licensed under the MIT License.

## Contact
For any inquiries, contact me at [romjanvr5@gmail.com](mailto:romjanvr5@gmail.com).

