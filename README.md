# ComplexHotelSystem

**ComplexHotelSystem** is a hotel management system developed as a full-stack application. The project focuses on modern software architecture, secure authentication, modularity and separation of responsibilities between the backend and frontend.

The application is designed to provide a foundation for managing hotel operations, user accounts and hotel-related services.

## Technologies

### Backend
- **Java 25**
- **Spring Boot 4.1.1**
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- MapStruct
- Maven
- JWT (JSON Web Token)
- Argon2 password hashing

### Frontend
- Vue 3
- TypeScript
- Vite
- HTML / CSS

## Architecture

The backend follows a modular architecture inspired by Clean Architecture principles. The application is divided into four main modules:

| Module | Responsibility |
|---|---|
| `domain` | Core business entities and business rules |
| `application` | Application logic and use cases |
| `infrastructure` | Database access and external integrations |
| `api` | REST API, controllers and application configuration |

The main goal of this structure is to separate business logic from technical implementation details and make the application easier to maintain, test and extend.

## Security

Security is an important part of the application.

Implemented security mechanisms include:

- Spring Security for authentication and authorization.
- JWT-based authentication.
- HttpOnly cookies for storing authentication tokens.
- CSRF protection for securing state-changing requests.
- Argon2 password hashing.
- Role-based access control.
- Password recovery using a temporary six-digit verification code.
- Expiration of password recovery codes.

## Backend Features

- REST API architecture.
- User registration and authentication.
- User account management.
- Password recovery.
- Role-based authorization.
- Database persistence using PostgreSQL.
- Object mapping using MapStruct.

## Frontend

The frontend is built with Vue 3 and TypeScript.

It communicates with the backend through REST endpoints and uses cookie-based authentication.

Current frontend functionality includes:
- Login page.
- Registration page.
- Password recovery.
- Password reset confirmation.
- Navigation components.

## Requirements

To run the project locally, you need:

- Java 25 or compatible JDK.
- Maven.
- PostgreSQL.
- Node.js and npm.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/ondrakodat/ComplexHotelSystem.git
cd ComplexHotelSystem
```

### 2. Configure the database

Create a PostgreSQL database and configure the database connection in the backend configuration (`application.properties`).

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hotelDb
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 3. Build the backend

```bash
mvn clean install
```

Run the Spring Boot application from the `api` module using your IDE or the appropriate Maven configuration.

### 4. Start the frontend

Navigate to the frontend directory:

```bash
cd frontend
npm install
npm run dev
```

The frontend development server is available at:

```text
http://localhost:5173
```

## Project Status

The project is currently under active development.

New functionality, improvements to security and additional hotel management features are being implemented.

## Author

**Ondřej Kodat**

GitHub: [@ondrakodat](https://github.com/ondrakodat)
