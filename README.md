# ByteClass API

**ByteClass** is a modular, feature-based Spring Boot application for managing e-learning courses, users, and interactive tasks. It showcases simple, but robust architecture, resilience, security, caching, and comprehensive testing.

---

## 📦 Main Features

* **User Module** (`/api/user` & `/api/auth/login`)

    * Register users (name, email, password)
    * Update, delete, list users
    * JWT-based authentication

* **Course Module** (`/api/course`)

    * Create, list, retrieve, update, delete courses
    * Publish courses (requires at least one task of each type and continuous order)

* **Task Module** (`/api/task`)

    * Create three task types for courses in **BUILDING** status:

        * **Open-text**
        * **Single-choice** (exactly one correct option)
        * **Multiple-choice** (at least two correct options)
    * List tasks by course, retrieve task details
    * Automatic reordering when inserting a task at an existing position

---

## 📐 Architecture

* **Feature-Based**
  Project is organized by feature (`user`, `course`, `task`), each containing its own controllers, services, repositories, DTOs, and mappers.

* **Clean Layers**

    * **Controller**: REST endpoints with validation, caching, security
    * **Application (Service / Strategy / Facade)**: business logic, transactions, resilience
    * **Repository**: Spring Data JPA interfaces
    * **Port (DTOs / Mappers)**: MapStruct for entity ↔ DTO mapping

* **Module Decoupling**
  Features interact via interfaces (`UserPort`, `CoursePort`, `TaskPort`), exchanging only DTOs and primitive IDs—no direct entity coupling.

* **Resilience**
  Resilience4j integration (`@Resilient` + AOP) for rate limiting and circuit breaking.

* **Security**
  Spring Security OAuth2 Resource Server with JWT; role hierarchy **ADMIN > INSTRUCTOR > STUDENT**.

* **Caching**
  Redis configured and used in controllers via `@Cacheable` / `@CacheEvict`.

* **Database Migrations**
  Flyway scripts for MySQL schema (`V1__…` through `V4__…_create_table_*.sql`).

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/…/byteclass/
│   │   ├── ByteClassApplication.java
│   │   ├── common/            # exceptions, handlers, utilities
│   │   ├── config/            # resilience, security, OpenAPI
│   │   └── feature/
│   │       ├── user/          # adapter/controller, service, domain, port
│   │       ├── course/        # adapter/controller, service, facade, domain, port
│   │       └── task/          # adapter/controller, service, strategies, domain, port
│   └── resources/
│       ├── db/migration/      # Flyway scripts
│       ├── application-*.properties
│       ├── app.key / app.pub
│       └── …
└── test/
    ├── java/…/byteclass/feature/
    │   ├── user/…             # UserControllerTest, UserServiceImplTest, etc.
    │   ├── course/…           # CourseControllerTest, CourseServiceImplTest, facade tests
    │   └── task/…             # TaskControllerTest, TaskServiceImplTest, strategy tests
    └── resources/
        └── application-test.properties (H2 in-memory)
```

---

## 🛠️ Technology Stack

* **Language & Framework**: Java 21, Spring Boot 3
* **Persistence**: Spring Data JPA (MySQL)
* **Migrations**: Flyway
* **Security**: Spring Security (OAuth2 Resource Server + JWT)
* **Resilience**: Resilience4j (rate limiter, circuit breaker)
* **Mapping & Annotations**: Lombok, MapStruct
* **Caching**: Spring Data Redis
* **API Docs**: Springdoc OpenAPI (Swagger UI)
* **Build & Orchestration**: Maven, Docker Compose
* **Testing**: JUnit 5, Mockito, AssertJ

---

## 🚀 Getting Started

1. **Prerequisites**

    * Docker & Docker Compose
    * Java 21+

2. **Generate RSA Keys**

   ```bash
   ./generate_keys.sh
   ```

3. **Start Infrastructure**

   ```bash
   docker-compose up -d
   ```

4. **Build & Run**

   ```bash
   ./mvnw clean package
   ./mvnw spring-boot:run
   ```

5. **Access**

    * **API Base**: `http://localhost:8080/api`
    * **Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testing

* **Unit Tests**: JUnit 5 + Mockito + AssertJ
* **Coverage**: Services, Controllers, Strategies, Facades, Repositories
* **Test Profile**: `application-test.properties` uses H2 in-memory database

---