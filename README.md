# 📋 Taskflow

<div align="center">

**A modern, secure task management application built with Spring Boot**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## 🌟 Overview

Taskflow is a simple yet powerful to-do list application designed to help users organize their daily tasks and boost productivity. Built with modern web technologies, it features a sleek glassmorphic UI, secure JWT authentication, and email verification for enhanced security.

## ✨ Features

### 🔐 Authentication & Security
- **User Registration** with email verification
- **Secure Login** with JWT token-based authentication
- **Email Verification** via 6-digit OTP codes
- **Password Encryption** using BCrypt
- **HttpOnly Cookies** for secure token storage
- **CSRF Protection** enabled by default

### ✅ Task Management
- **Create Tasks** - Add new tasks quickly
- **Toggle Status** - Mark tasks as complete/incomplete
- **Delete Tasks** - Remove tasks you no longer need
- **User Isolation** - Each user sees only their own tasks
- **Real-time Updates** - Instant UI feedback

### 🎨 User Interface
- **Modern Glassmorphic Design** with gradient accents
- **Responsive Layout** - Works on desktop, tablet, and mobile
- **Smooth Animations** - Polished user experience
- **Custom Scrollbars** - Styled for aesthetic consistency
- **Empty State Handling** - Helpful UI when no tasks exist

### 📧 Email Integration
- **Styled Verification Emails** with modern HTML templates
- **10-Minute Code Expiry** for security
- **Resend Code Functionality** for user convenience

---

## 🛠️ Tech Stack

### Backend
- **Spring Boot 4.0.2** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **Hibernate** - ORM implementation
- **JWT (jjwt 0.11.5)** - Token-based authentication
- **Java Mail Sender** - Email functionality

### Frontend
- **Thymeleaf** - Server-side templating
- **Bootstrap 5.3.8** - UI framework
- **Bootstrap Icons 1.13.1** - Icon library
- **Custom CSS** - Glassmorphic design system

### Database
- **PostgreSQL** - Production database

### Tools & Dependencies
- **Maven** - Dependency management
- **Lombok** - Boilerplate code reduction
- **Spring DevTools** - Development productivity

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.9+**
- **PostgreSQL 12+**
- **Git**
- A **Gmail account** (for email functionality)

---

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/taskflow.git
cd taskflow
```

### 2. Configure PostgreSQL Database

Create a new PostgreSQL database:

```sql
CREATE DATABASE taskflowdb;
```

### 3. Set Up Environment Variables

Create a `.env` file in the project root directory:

```properties
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/taskflowdb
SPRING_DATASOURCE_USERNAME=your_postgres_username
SPRING_DATASOURCE_PASSWORD=your_postgres_password

# JWT Configuration
JWT_SECRET_KEY=your_base64_encoded_secret_key_here

# Email Configuration
SUPPORT_EMAIL=your_gmail_address@gmail.com
APP_PASSWORD=your_gmail_app_password
```

#### 🔑 Generating JWT Secret Key

Generate a secure Base64-encoded key:

```bash
# Using OpenSSL
openssl rand -base64 32

# Or online at https://www.base64encode.org/
```

#### 📧 Gmail App Password Setup

1. Enable **2-Factor Authentication** on your Gmail account
2. Go to [Google App Passwords](https://myaccount.google.com/apppasswords)
3. Generate a new app password for "Mail"
4. Copy the 16-character password to `APP_PASSWORD` in `.env`

### 4. Build the Project

```bash
./mvnw clean install
```

### 5. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on **http://localhost:9090**

---

## 📖 Usage

### Creating an Account

1. Navigate to **http://localhost:9090/auth/signup**
2. Fill in your details (name, email, password)
3. Click **Create Account**
4. Check your email for the 6-digit verification code
5. Enter the code on the verification page
6. You'll be redirected to the login page

### Logging In

1. Go to **http://localhost:9090/auth/login**
2. Enter your email and password
3. Click **Sign In**
4. You'll be redirected to your task dashboard

### Managing Tasks

- **Add a Task**: Type in the input field and click the `+` button
- **Complete a Task**: Click the circle icon next to the task
- **Delete a Task**: Hover over the task and click the trash icon

---

## 🗂️ Project Structure

```
taskflow/
├── src/
│   ├── main/
│   │   ├── java/com/example/taskflow/
│   │   │   ├── auth/              # Authentication module
│   │   │   │   ├── controller/    # Auth endpoints
│   │   │   │   ├── dto/           # Data transfer objects
│   │   │   │   ├── email/         # Email service
│   │   │   │   └── service/       # Authentication logic
│   │   │   ├── config/            # Security & app configuration
│   │   │   ├── exception/         # Custom exceptions
│   │   │   ├── task/              # Task management module
│   │   │   │   ├── controller/    # Task endpoints
│   │   │   │   ├── entity/        # Task entity
│   │   │   │   └── repository/    # Data access layer
│   │   │   └── user/              # User module
│   │   │       ├── controller/    # User endpoints
│   │   │       ├── dto/           # User DTOs
│   │   │       ├── entity/        # User entity
│   │   │       ├── repository/    # User data access
│   │   │       └── service/       # User & JWT services
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/           # Stylesheets
│   │       │   └── images/        # Static images
│   │       ├── templates/         # Thymeleaf templates
│   │       └── application.properties
│   └── test/                      # Unit & integration tests
├── .env                           # Environment variables
├── pom.xml                        # Maven dependencies
└── README.md
```

---

## 🔒 Security Features

### Authentication Flow
1. User registers → Email verification code sent
2. User verifies email → Account enabled
3. User logs in → JWT token generated
4. Token stored in **HttpOnly cookie**
5. Every request validated via `JwtAuthenticationFilter`

### Security Measures
- **Password Hashing**: BCrypt algorithm
- **JWT Tokens**: Signed with HS256
- **CSRF Protection**: Enabled for all forms
- **HttpOnly Cookies**: Prevents XSS attacks
- **User Isolation**: Users can only access their own tasks
- **Code Expiration**: Verification codes expire in 10 minutes

---

## 🎯 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/login` | Login page |
| POST | `/auth/login` | Authenticate user |
| GET | `/auth/signup` | Registration page |
| POST | `/auth/signup` | Create new account |
| GET | `/auth/verify` | Verification page |
| POST | `/auth/verify` | Verify email with OTP |
| GET | `/auth/resend` | Resend code page |
| POST | `/auth/resend` | Resend verification code |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` or `/home` | Task dashboard |
| POST | `/add` | Create new task |
| POST | `/update/{id}` | Toggle task status |
| POST | `/delete/{id}` | Delete task |

### Users (REST API)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/users/me` | Get current user | Required |
| GET | `/users` | Get all users | Admin only |

---

## 🎨 Customization

### Changing Colors

Edit `src/main/resources/static/css/style.css`:

```css
/* Primary gradient */
background: linear-gradient(135deg, #6366f1, #22d3ee);

/* Background */
background: radial-gradient(circle at top, #1f2933, #0b0f14);
```

### Email Template

Modify `src/main/java/com/example/taskflow/auth/email/EmailService.java`:

```java
private String buildEmailHtml(String code) {
    // Customize HTML here
}
```

---


## 🐛 Troubleshooting

### Email Not Sending

1. Verify your Gmail has **2FA enabled**
2. Check your **App Password** is correct
3. Ensure no firewall is blocking port 587
4. Check application logs for detailed errors

### Database Connection Issues

1. Verify PostgreSQL is running:
   ```bash
   sudo service postgresql status
   ```
2. Check database credentials in `.env`
3. Ensure database `taskflowdb` exists

### Port Already in Use

Change the port in `application.properties`:

```properties
server.port=8080
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Bootstrap](https://getbootstrap.com/) - UI components
- [PostgreSQL](https://www.postgresql.org/) - Database
- [JWT.io](https://jwt.io/) - Token implementation
- Design inspiration from modern glassmorphic UI trends

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made by Mohamed Mostafa

</div>