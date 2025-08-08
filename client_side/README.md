# E-Commerce Client Side

A modern e-commerce frontend built with React, Vite, and Tailwind CSS that connects to a Spring Boot backend.

## Features

- 🛍️ **Product Browsing**: Browse and search products
- 🛒 **Shopping Cart**: Add, remove, and manage cart items
- 👤 **User Authentication**: Login and registration with JWT tokens
- 📦 **Order Management**: View order history and track order status
- 📱 **Responsive Design**: Mobile-first design that works on all devices
- ⚡ **Fast Performance**: Built with Vite for optimal development and build performance

## Tech Stack

- **Frontend Framework**: React 18
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **Routing**: React Router DOM
- **HTTP Client**: Axios
- **Icons**: Heroicons
- **Notifications**: React Hot Toast
- **State Management**: React Context API

## Prerequisites

- Node.js (version 16 or higher)
- npm or yarn
- The Spring Boot backend server running on `http://localhost:8080`

## Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build

## Project Structure

```
src/
├── components/          # Reusable UI components
│   └── Header.jsx      # Navigation header
├── context/            # React Context providers
│   ├── AuthContext.jsx # Authentication state
│   └── CartContext.jsx # Shopping cart state
├── services/           # API services
│   └── api.js         # HTTP client and API endpoints
├── App.jsx            # Main application component
└── main.jsx          # Application entry point
```

## API Endpoints

The frontend connects to the following backend endpoints:

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID

### Cart
- `GET /api/cart/{userId}` - Get user's cart
- `POST /api/cart/{userId}/add/{productId}` - Add item to cart
- `PUT /api/cart/{userId}/update/{productId}` - Update cart item quantity
- `DELETE /api/cart/{userId}/remove/{productId}` - Remove item from cart
- `DELETE /api/cart/{userId}/clear` - Clear cart

### Orders
- `GET /api/orders/user/{userId}` - Get user's orders
- `POST /api/orders/checkout/{userId}` - Checkout cart

## Current Status

The application is now running successfully with:
- ✅ Basic routing setup
- ✅ Tailwind CSS working properly
- ✅ Authentication context
- ✅ Cart context
- ✅ API service layer
- ✅ Header component with navigation
- ✅ Basic page components (Home, Products, Cart, Orders, Login, Register)
- ✅ Protected routes
- ✅ Toast notifications

## Next Steps

To complete the full e-commerce functionality, you can:

1. Implement the full product listing with search and filtering
2. Add product detail pages
3. Complete the shopping cart functionality
4. Implement the checkout process
5. Add order management features
6. Enhance the authentication forms with validation
7. Add admin features for product management

## Support

For support, please ensure your Spring Boot backend is running on `http://localhost:8080` before starting the frontend application.
