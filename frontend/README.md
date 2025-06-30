# 🛒 RestMart – Front‑End

RestMart is a modern, responsive e‑commerce front‑end built with **React** and designed to pair seamlessly with the Django REST Framework back‑end. It offers a smooth shopping experience with authentication, cart and order management, and a secure SSLCommerz payment flow.

---

## 🚀 Live Demo

👉 [https://restmart-client.vercel.app/](https://restmart-client.vercel.app/)

---

## 🔧 Tech Stack

| Tool             | Purpose                       |
| ---------------- | ----------------------------- |
| **React JS**     | Component‑based UI library    |
| **React Router** | Client‑side routing           |
| **Tailwind CSS** | Utility‑first styling         |
| **Daisy UI**     | Pre‑built Tailwind components |
| **HTML5**        | Semantic markup               |
| **SSLCommerz**   | Payment gateway integration   |

---

## ✨ Features

- **🔐 Authentication** – JWT‑based login & registration
- **🛍️ Shop Page** – search, category filters & pagination
- **🛒 Cart Management** – add, update quantity & remove items
- **📦 Orders** – place orders & track status in real time
- **💳 Payments** – secure checkout via **SSLCommerz**
- **⭐ Reviews** – verified buyers can post, edit & delete reviews
- **📱 Responsive** – mobile‑first, works on all devices

---

## Demo Credentials

### Admin Access

```plaintext
Email: admin@example.com
Password: DemoAdmin123
```

### Customer Access

```plaintext
Email: test@example.com
Password: DemoUser123
```

## 📁 Project Structure

```text
restmart-frontend/
├── public/
├── src/
│   ├── assets/
│   ├── components/
│   ├── context/
│   ├── hooks/
│   ├── layout/
│   ├── pages/
│   ├── routes/
│   ├── services/
│   └── App.jsx
├── tailwind.config.js
├── postcss.config.js
├── .env.example
├── package.json
└── README.md
```

---

## 🛠️ Setup & Installation

> **Prerequisite:** Node.js ≥ 18

1. **Clone the repo**

   ```bash
   git clone https://github.com/romjanxr/restmart-frontend.git
   cd restmart-frontend
   ```

2. **Install dependencies**

   ```bash
   npm install
   # or
   yarn
   ```

3. **Run the development server**

   ```bash
   npm run dev   # default Vite command
   ```

   The app runs at **[http://localhost:5173](http://localhost:5173)** by default.

4. **Production build**

   ```bash
   npm run build && npm run preview
   ```

---

> **Developed by [Md Romjan Ali](https://github.com/romjanxr).**
>
> Looking for the back‑end? Check out the [RestMart‑DRF repository](https://github.com/romjanxr/RestMart).
