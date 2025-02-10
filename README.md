# DRF RestMart E-Commerce API

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
- **Poetry** (for dependency management)

## Installation

### Prerequisites
Ensure you have the following installed:
- Python (>= 3.8)
- PostgreSQL
- Poetry (for dependency management)

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/romjanxr/RestMart.git
   cd RestMart
   ```

2. **Install Dependencies using Poetry**
   ```bash
   poetry install
   ```

3. **Activate the Virtual Environment**

   * **Linux/macOS:**

     ```bash
     source $(poetry env info --path)/bin/activate
     ```

   * **Windows:**

     ```powershell
     $(poetry env info --path)\Scripts\Activate.ps1  # For PowerShell
     $(poetry env info --path)\Scripts\activate     # For Command Prompt (cmd.exe)
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


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
This project is licensed under the MIT License.

## Contact
For any inquiries, contact me at [romjanvr5@gmail.com](mailto:romjanvr5@gmail.com).

