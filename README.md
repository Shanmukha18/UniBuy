# UniBuy - E-Commerce Platform

A modern, full-stack e-commerce platform built with React (Frontend) and Spring Boot (Backend), featuring user authentication, product management, shopping cart functionality, order processing, and secure payment integration with Razorpay.

## Technology Stack

### Frontend
- **React 19** - Modern React with hooks and functional components
- **Vite** - Fast build tool and development server
- **React Router DOM** - Client-side routing
- **Tailwind CSS** - Utility-first CSS framework
- **Axios** - HTTP client for API calls
- **React Hot Toast** - Toast notifications
- **Heroicons** - Beautiful SVG icons
- **Razorpay** - Payment gateway integration

### Backend
- **Spring Boot 3.5.4** - Java-based backend framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **PostgreSQL** - Relational database
- **JWT** - JSON Web Tokens for authentication
- **Lombok** - Java boilerplate code reduction
- **Maven** - Dependency management and build tool

### DevOps & Deployment
- **Vercel** - Frontend deployment
- **Render** - Backend deployment
- **PostgreSQL** - Cloud database (Render)
- **Environment Variables** - Secure configuration management

## Quick Start

### Prerequisites
- Node.js (v18 or higher)
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database

### Frontend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd e-commerce/client_side
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Environment configuration**
   ```bash
   cp env.example .env.local
   ```
   Update the environment variables in `.env.local`:
   ```env
   VITE_API_BASE_URL=http://localhost:8081/api
   ```

4. **Start development server**
   ```bash
   npm run dev
   ```
   The frontend will be available at `http://localhost:5173`

### Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd ../server_side
   ```

2. **Database setup**
   - Create a PostgreSQL database
   - Update `application.properties` with your database credentials

3. **Environment variables**
   Create `application.properties` or set environment variables:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   jwt.secret=your-super-secret-jwt-key-change-this-in-production-32bytes-minimum
   razorpay.key.id=your_razorpay_key_id
   razorpay.key.secret=your_razorpay_key_secret
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The backend will be available at `http://localhost:8081`


## 🔧 Configuration

### Environment Variables

#### Frontend (.env.local)
```env
VITE_API_BASE_URL=http://localhost:8081/api
```

#### Backend (application.properties)
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your-super-secret-jwt-key-change-this-in-production-32bytes-minimum
jwt.expiration=86400000

# Razorpay Configuration
razorpay.key.id=your_razorpay_key_id
razorpay.key.secret=your_razorpay_key_secret

# CORS Configuration
cors.allowed-origins=http://localhost:5173,https://your-production-domain.com
```


## Security Features

- **JWT Authentication** - Secure token-based authentication
- **CORS Configuration** - Controlled cross-origin resource sharing
- **Password Encryption** - BCrypt password hashing
- **Input Validation** - Request validation and sanitization
- **Environment Variables** - Secure configuration management
- **HTTPS** - Secure communication in production

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create product (Admin)
- `PUT /api/products/{id}` - Update product (Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

### Cart
- `GET /api/cart/{userId}` - Get user's cart
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/update` - Update cart item
- `DELETE /api/cart/remove/{itemId}` - Remove item from cart

### Orders
- `GET /api/orders/{userId}` - Get user's orders
- `POST /api/orders` - Place new order
- `PUT /api/orders/{orderId}/status` - Update order status

### Payments
- `POST /api/payments/create-order` - Create payment order
- `POST /api/payments/verify` - Verify payment

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [React](https://reactjs.org/) - Frontend library
- [Tailwind CSS](https://tailwindcss.com/) - CSS framework
- [Razorpay](https://razorpay.com/) - Payment gateway
- [Vercel](https://vercel.com/) - Frontend deployment
- [Render](https://render.com/) - Backend deployment

