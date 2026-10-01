# E-Commerce REST API

A backend-focused e-commerce REST API built with **Java 17 and Spring Boot 4.0.0**.

The project demonstrates a complete backend workflow around authentication, authorization, product and category management, inventory, shopping carts, order placement, address management, validation, persistence, centralized exception handling, JWT security, and OpenAPI documentation.

---

## Features

### Authentication & User Management

- JWT-based authentication
- Stateless Spring Security configuration
- Role-based authorization with `USER` and `ADMIN` roles
- User registration
- Login and logout
- Retrieve the currently authenticated user
- Email verification
- Password reset flow
- Verification-token requests
- User profile update
- Password hashing with Spring Security

### Product Management

- Create, update, and delete products
- Retrieve products by ID
- Product search
- Filter products by category
- Filter products by price range
- Pagination and sorting
- Assign products to categories
- Soft deletion

### Category Management

- Create categories
- Retrieve all categories
- Retrieve a category by ID
- Update categories
- Delete categories
- Soft deletion

### Inventory

- Product-specific inventory
- Stock quantity tracking
- Optimistic locking using JPA `@Version`
- Stock validation during order placement
- Automatic stock deduction when an order is placed

### Shopping Cart

- Create a cart
- Retrieve the current active cart
- Add products to the cart
- Remove individual cart items
- Delete the current cart
- Track the price at which an item was added
- Cart status management

### Orders

- Place an order from the authenticated user's active cart
- Select a delivery address
- Validate available inventory
- Deduct inventory when an order is placed
- Calculate order totals
- Retrieve the authenticated user's orders
- Retrieve an individual order
- Track order status

### Persistence & Data Access

- Spring Data JPA
- Hibernate
- Microsoft SQL Server
- JPA Specifications for dynamic product filtering
- Auditing through a reusable base entity
- Soft-delete flags on relevant entities

### API Documentation

- OpenAPI / Swagger UI
- JWT Bearer authentication scheme documented in Swagger
- Endpoint descriptions and response documentation

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.0.0 | Application framework |
| Spring Web MVC | REST API |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| Spring Security | Authentication & authorization |
| Auth0 `java-jwt` 4.5.0 | JWT creation and validation |
| Microsoft SQL Server | Relational database |
| Spring Validation | Request validation |
| Spring Mail | Email verification/password-reset emails |
| ModelMapper | DTO/entity mapping |
| Lombok | Boilerplate reduction |
| Springdoc OpenAPI 3.0.0 | Swagger / OpenAPI documentation |
| Maven | Build and dependency management |
| Logback | Application logging |

---

## Architecture

The application follows a layered Spring architecture:

```text
                    ┌─────────────────────┐
                    │       Client        │
                    └──────────┬──────────┘
                               │ HTTP/JSON
                               ▼
                    ┌─────────────────────┐
                    │    Controllers      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │      Services       │
                    │   Business Logic    │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Repositories     │
                    │   Spring Data JPA   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    SQL Server DB    │
                    └─────────────────────┘

        Security / JWT
              │
              ▼
       JwtRequestFilter
              │
              ▼
     SecurityFilterChain
```

### Main layers

**Controllers**

Expose the REST API and handle HTTP requests/responses.

**Services**

Contain application and business logic such as authentication, product management, cart operations, inventory handling, and order processing.

**Repositories**

Provide persistence operations through Spring Data JPA.

**DTOs**

Separate API request/response models from JPA entities.

**Entities**

Represent the persistent domain model.

**Security**

Handles JWT validation, authentication, authorization, and protected endpoints.

---

## Domain Model

The main entities are:

```text
LocalUser
   │
   ├── UserRoles
   ├── Addresses
   ├── VerificationTokens
   └── Carts
          │
          └── CartItems
                 │
                 └── Product
                        │
                        ├── Category
                        └── Inventory

LocalUser
   │
   └── WebOrder
          │
          ├── Address
          └── OrderItems
                 │
                 └── Product
```

### Core entities

- `LocalUser`
- `UserRoles`
- `Address`
- `VerificationToken`
- `LoginTokens`
- `Product`
- `Category`
- `Inventory`
- `Cart`
- `CartItem`
- `WebOrder`
- `OrderItem`

---

## Security

The API uses **stateless JWT authentication**.

The security flow is:

```text
Login
  │
  ▼
Authenticate credentials
  │
  ▼
Generate JWT
  │
  ▼
Client stores token
  │
  ▼
Authorization: Bearer <JWT>
  │
  ▼
JwtRequestFilter
  │
  ▼
Validate JWT
  │
  ▼
Load authenticated user
  │
  ▼
Spring Security authorization
  │
  ▼
Controller
```

JWTs contain the authenticated username and role information and are signed using an HMAC algorithm.

The configured normal access-token lifetime is **3600 seconds (1 hour)**.

Separate JWT-based flows are also used for:

- Email verification — 24 hours
- Password reset — 30 minutes

> JWT secrets, database passwords, and mail credentials should be supplied through environment-specific configuration and must not be committed to source control.

---

## Roles

The application uses two primary roles:

| Role | Access |
|---|---|
| `USER` | Authenticated user operations |
| `ADMIN` | Administrative product/category operations and authenticated user operations |

Administrative product and category mutations are protected using method-level authorization.

---

## REST API

The application runs on port `7070` by default.

Base URL:

```text
http://localhost:7070
```

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a user |
| `POST` | `/auth/login` | Authenticate and obtain JWT |
| `GET` | `/auth/me` | Get current authenticated user |
| `GET` | `/auth/verify` | Verify user email |
| `POST` | `/auth/forgot-password` | Request password reset |
| `POST` | `/auth/reset-password` | Reset password |
| `POST` | `/auth/request-verify` | Request email verification |
| `PUT` | `/auth/update/{userId}` | Update user |
| `POST` | `/auth/logout` | Logout / invalidate login token |

### Products

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/products/get` | USER / ADMIN | Get products with filtering, pagination and sorting |
| `GET` | `/products/{id}` | USER / ADMIN | Get product by ID |
| `POST` | `/products/add` | ADMIN | Create product |
| `PUT` | `/products/{id}` | ADMIN | Update product |
| `DELETE` | `/products/{id}` | ADMIN | Delete product |
| `GET` | `/products/search` | USER / ADMIN | Search products |
| `PATCH` | `/products/update-category/{productId}` | ADMIN | Update product category |

### Product Filtering

The product listing endpoint supports:

- Category filtering
- Minimum price
- Maximum price
- Sorting field
- Sort direction
- Pagination

Example:

```http
GET /products/get?priceMin=100&priceMax=1000&sortBy=name&sortDir=asc
```

Dynamic filtering is implemented using Spring Data JPA `Specification`s.

Current product specifications include:

```text
isNotDeleted()
hasCategory(...)
priceBetween(...)
```

### Categories

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/category/get` | USER / ADMIN | Get all categories |
| `GET` | `/category/get/{id}` | USER / ADMIN | Get category |
| `POST` | `/category/add` | ADMIN | Create category |
| `PUT` | `/category/update/{id}` | ADMIN | Update category |
| `DELETE` | `/category/delete/{id}` | ADMIN | Delete category |

### Cart

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/cart/get` | USER / ADMIN | Get current active cart |
| `POST` | `/cart/create-cart` | USER / ADMIN | Create a new cart |
| `POST` | `/cart/add-item` | USER / ADMIN | Add a product to the cart |
| `DELETE` | `/cart/delete-item/{itemId}` | USER / ADMIN | Remove a cart item |
| `DELETE` | `/cart/delete-cart` | USER / ADMIN | Delete the current cart |

### Orders

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/orders/place?addressId={id}` | USER / ADMIN | Place order from active cart |
| `GET` | `/orders/my-orders` | USER / ADMIN | Get current user's orders |
| `GET` | `/orders/{orderId}` | USER / ADMIN | Get a specific user's order |

When an order is placed, the service:

1. Retrieves the user's active cart.
2. Validates that the cart is not empty.
3. Validates inventory availability.
4. Deducts the ordered quantities from inventory.
5. Creates the order and order items.
6. Calculates the total price.
7. Marks the cart/items as deleted.

The operation is transactional.

---

## Order Status

Orders use the following statuses:

```text
CREATED
PENDING
PAID
SHIPPED
CANCELLED
```

New orders are currently created with:

```text
PENDING
```

## Cart Status

Carts use:

```text
ACTIVE
CHECKED_OUT
CANCELED
```

---

## Inventory & Concurrency

Each product has a dedicated `Inventory` entity.

Inventory contains:

- Product reference
- Available quantity
- Soft-delete flag
- JPA version field

The version field enables **optimistic locking**:

```java
@Version
private Long version;
```

During order placement, the service checks available inventory before deducting the requested quantity.

---

## Soft Deletion

Several entities use an `isDeleted` flag instead of immediately removing records from the database.

This is used for entities such as:

- Products
- Categories
- Carts
- Cart items
- Orders
- Order items
- Users
- Inventory

This approach preserves database records while allowing application-level filtering of inactive/deleted data.

---

## Auditing

Entities inherit from:

```text
BaseAuditEntity
```

The project configures JPA auditing and an `AuditorAware` implementation to provide common audit information across entities.

This avoids duplicating auditing infrastructure in every entity.

---

## Exception Handling

The application contains a centralized:

```text
GlobalExceptionHandler
```

and domain-specific exceptions such as:

```text
ProductNotFoundException
CategoryNotFoundException
UserNotFoundException
UserAlreadyExistsException
InvalidCredentialsException
UserIsNotVerifiedException
TokenNotFoundException
InsufficientStockException
CartIsEmptyException
ItemNotFoundException
AddressNotFoundException
PasswordMismatchException
EmailFailureException
```

This keeps error handling consistent across REST endpoints.

---

## API Documentation

Swagger / OpenAPI is configured through `SwaggerConfig`.

Once the application is running, open:

```text
http://localhost:7070/swagger-ui/index.html
```

OpenAPI JSON is available through:

```text
http://localhost:7070/v3/api-docs
```

The API defines a JWT Bearer security scheme named:

```text
bearerAuth
```

In Swagger UI, authenticate using:

```text
Bearer <your-jwt>
```

---

## Configuration

The application uses Microsoft SQL Server.

The default development configuration expects:

```text
SQL Server instance: localhost\SQLEXPRESS
Database: ecommerce
Application port: 7070
```

The application uses Hibernate's update strategy during development:

```properties
spring.jpa.hibernate.ddl-auto=update
```

### Required configuration

Sensitive values should be provided through environment-specific configuration.

Important properties include:

```properties
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...

jwt.algorithm.key=...
jwt.issuer=...
jwt.expiryInSeconds=3600

spring.mail.username=...
spring.mail.password=...
```

Do not commit real credentials or cryptographic secrets to the repository.

---

## Running the Project

### Prerequisites

Install:

- Java 17 or later
- Maven
- Microsoft SQL Server
- SQL Server Express is supported by the default development configuration

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

### 1. Clone the repository

```bash
git clone <repository-url>
cd Ecommerce
```

### 2. Create the database

Create a SQL Server database named:

```text
ecommerce
```

### 3. Configure credentials

Configure the database, JWT signing key, and mail credentials in your local environment/configuration.

### 4. Build

Using the Maven wrapper:

**Windows**

```cmd
mvnw.cmd clean install
```

**Linux/macOS**

```bash
./mvnw clean install
```

Or use Maven directly:

```bash
mvn clean install
```

### 5. Run

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/Ecommerce-0.0.1-SNAPSHOT.jar
```

The API will be available at:

```text
http://localhost:7070
```

---

## Project Structure

```text
src/
├── main/
│   ├── java/com/learn/ecommerce/
│   │
│   ├── config/
│   │   ├── security/
│   │   │   └── JwtRequestFilter.java
│   │   ├── JpaAuditConfig.java
│   │   ├── ModelMapperConfig.java
│   │   ├── PaginationConfig.java
│   │   ├── SwaggerConfig.java
│   │   └── WebSecurityConfig.java
│   │
│   ├── controller/
│   │   ├── auth/
│   │   ├── cart/
│   │   ├── category/
│   │   ├── order/
│   │   └── product/
│   │
│   ├── DTO/
│   │   ├── Address/
│   │   ├── Cart/
│   │   ├── CartItem/
│   │   ├── Category/
│   │   ├── Order/
│   │   ├── ProductDTO/
│   │   ├── Roles/
│   │   ├── UserRequestDTO/
│   │   └── UserResponseDTO/
│   │
│   ├── entity/
│   │
│   ├── enums/
│   │
│   ├── exceptionhandler/
│   │
│   ├── repository/
│   │   └── JpaQueryLogic/
│   │
│   ├── services/
│   │
│   └── utils/
│
└── resources/
    ├── application.properties
    └── logback-spring.xml
```

---

## Development Notes

This project intentionally separates:

```text
Entity
   ↓
Repository
   ↓
Service
   ↓
Controller
   ↓
DTO
```

The application also keeps security concerns separate from business logic through:

- `WebSecurityConfig`
- `JwtRequestFilter`
- `JwtService`
- `LocalUserDetailsService`

DTO mapping is handled through ModelMapper, while domain-specific query logic is implemented using JPA Specifications.

---

## Future Improvements

Potential extensions include:

- Refresh-token architecture
- Redis caching
- Payment gateway integration
- Kafka-based asynchronous order processing
- Docker / Docker Compose
- Automated integration tests with Testcontainers
- CI/CD pipeline
- Cloud deployment
- Rate limiting
- Product image storage
- Advanced search
- Inventory administration endpoints
- Order administration and status-management endpoints
- API versioning
- Production database migrations using Flyway or Liquibase
- Improved test coverage

---

## Project Purpose

This project was built as a backend learning and portfolio project to practice building a non-trivial Spring application beyond basic CRUD.

It focuses on practical backend concepts including:

- REST API design
- Spring Boot architecture
- Spring Security
- JWT authentication
- Role-based access control
- JPA/Hibernate
- SQL Server
- Entity relationships
- DTO-based API design
- Dynamic queries with Specifications
- Pagination and sorting
- Inventory management
- Transactional order processing
- Soft deletion
- Auditing
- Validation
- Centralized exception handling
- API documentation
- Application logging

---

## Author

**Mohamed Salah Abd-Allah Mostafa**

Java / Spring Boot Backend Developer  
Embedded Systems Engineer

GitHub: `https://github.com/MohamedSalah10010`

---

## License

This project is currently intended as an educational and portfolio project.
