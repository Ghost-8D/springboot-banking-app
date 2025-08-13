# Secure Banking Transaction API

A robust Spring Boot application providing secure banking operations with JWT authentication, comprehensive error handling, and complete API documentation.

## Features

- **JWT Authentication** - Secure user registration and login
- **Account Management** - View account balance and details
- **Transaction Operations** - Deposits, withdrawals, and transfers between accounts
- **Transaction History** - Complete transaction tracking
- **Comprehensive Error Handling** - Structured error responses for all scenarios
- **API Documentation** - Interactive Swagger UI with detailed examples
- **PostgreSQL Integration** - Production-ready database setup
- **Comprehensive Testing** - Unit and integration tests

## Tech Stack

- **Java 17+**
- **Spring Boot 3.5.4**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Primary database
- **H2** - Testing database
- **JWT (JJWT)** - Token-based authentication
- **Swagger/OpenAPI 3** - API documentation
- **Maven** - Dependency management
- **JUnit 5** - Testing framework

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

## Setup and Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Ghost-8D/springboot-banking-app.git
cd springboot-banking-app
```

### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE banking_app;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE banking_app TO postgres;
```

### 3. Configure Application

Update `src/main/resources/application.properties` if needed:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/banking_app
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT Configuration
jwt.secret=mySecretKey12345678901234567890123456789012345678901234567890
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### 4. Build and Run

```bash
# Install dependencies and run tests
mvn clean install

# Start the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification
Raw OpenAPI JSON available at:
```
http://localhost:8080/v3/api-docs
```

## Authentication

### 1. Register a new user
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securepassword123"
}
```

### 2. Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securepassword123"
}
```

### 3. Use JWT Token
Include the JWT token in subsequent requests:
```bash
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### Account Management
- `GET /api/account/balance` - Get account balance

### Transactions
- `POST /api/transactions/deposit` - Deposit money
- `POST /api/transactions/withdraw` - Withdraw money
- `POST /api/transactions/transfer` - Transfer between accounts
- `GET /api/transactions/history` - Get transaction history

## Example Usage

### Make a Deposit
```bash
POST /api/transactions/deposit
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 500.00,
  "description": "Salary deposit"
}
```

### Make a Withdrawal
```bash
POST /api/transactions/withdraw
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 200.00,
  "description": "ATM withdrawal - Main Street"
}
```

### Transfer Money
```bash
POST /api/transactions/transfer
Authorization: Bearer <token>
Content-Type: application/json

{
  "targetAccountId": 2,
  "amount": 150.00,
  "description": "Payment to John"
}
```

## Error Handling

The API returns structured error responses:

```json
{
  "status": 400,
  "error": "INSUFFICIENT_FUNDS",
  "message": "Insufficient balance for withdrawal",
  "details": "Account balance: $50.00, attempted withdrawal: $100.00",
  "path": "/api/transactions/withdraw",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Error Types
- `ACCOUNT_NOT_FOUND` (404) - Account doesn't exist
- `INSUFFICIENT_FUNDS` (400) - Not enough balance
- `INVALID_TRANSFER` (400) - Invalid transfer operation
- `USERNAME_ALREADY_EXISTS` (400) - Username taken during registration
- `INVALID_CREDENTIALS` (401) - Wrong username/password
- `VALIDATION_ERROR` (400) - Invalid request data

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=AuthControllerTest
```

### Test Coverage
The application includes:
- Unit tests for controllers
- Integration tests for services
- Repository tests
- Security configuration tests


## Security Features

- **JWT Authentication** - Stateless authentication
- **Password Encryption** - BCrypt hashing
- **CORS Configuration** - Cross-origin request handling
- **Input Validation** - Comprehensive request validation
- **SQL Injection Protection** - JPA prevents SQL injection
- **Exception Handling** - No sensitive data exposure


## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Open an issue on GitHub

## Version History

- **v0.0.0** - Initial release with core banking operations
- **v0.1.0** - Added comprehensive error handling and Swagger documentation
- **v0.2.0** - Enhanced security and test coverage
