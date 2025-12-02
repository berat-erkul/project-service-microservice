# Project Service - Microservice

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.4-brightgreen)

![Java](https://img.shields.io/badge/Java-11-orange)

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)

![Keycloak](https://img.shields.io/badge/Keycloak-OAuth2-red)

![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Hoxton.SR8-blue)

![License](https://img.shields.io/badge/License-MIT-yellow)

Enterprise-grade **Project Management Microservice** built with Spring Boot, featuring OAuth2 authentication via Keycloak, inter-service communication with Feign Client, service discovery with Eureka, and production-ready architecture.

> **Architecture:** This is a **microservice** component of the Ticketing Application ecosystem, designed for independent deployment, scalability, and distributed system capabilities.

---

## Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [API Documentation](#-api-documentation)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Security](#-security)
- [Inter-Service Communication](#-inter-service-communication)
- [Database Schema](#-database-schema)
- [API Endpoints](#-api-endpoints)
- [Microservices Ecosystem](#-microservices-ecosystem)


---

##  Overview

The **Project Service** is a dedicated microservice responsible for managing projects within the Ticketing Application ecosystem. It provides comprehensive project lifecycle management, including creation, updates, completion tracking, and integration with the Task Service for task-related operations.

### Use Cases

- **Project Management:** Create, update, and manage projects with detailed tracking
- **Project Status Tracking:** Monitor project status (Open, In Progress, Completed)
- **Manager Assignment:** Assign projects to managers with access control
- **Task Integration:** Retrieve task counts and manage related tasks through Task Service
- **Role-Based Operations:** Different capabilities for Admin and Manager roles

### Service Responsibilities

- **Project CRUD Operations:** Full lifecycle management of projects
- **Project Status Management:** Track and update project completion status
- **Manager-Project Association:** Link projects to assigned managers
- **Task Service Integration:** Communicate with Task Service for task-related operations
- **Access Control:** Enforce role-based and ownership-based access restrictions

---

##  Key Features

###  Security & Authentication

- **OAuth2 Integration** with Keycloak for enterprise-grade authentication
- **Role-Based Access Control (RBAC)** with Admin and Manager roles
- **JWT Token Validation** for stateless authentication
- **Token Propagation** via Feign Client Interceptor for inter-service calls
- **Access Control Enforcement** ensuring managers can only access their own projects

###  Architecture & Design

- **Microservice Architecture** with independent deployment capabilities
- **RESTful API Design** following industry best practices
- **Clean Architecture** with clear separation of concerns
- **DTO Pattern** for decoupling domain models from API contracts
- **Global Exception Handling** for consistent error responses
- **Entity Auditing** with BaseEntity pattern
- **Soft Delete Pattern** for data retention

###  Inter-Service Communication

- **Feign Client** for declarative REST client communication
- **Service Discovery** via Eureka for dynamic service location
- **Load Balancing** with Spring Cloud LoadBalancer
- **Token Propagation** for authenticated inter-service calls
- **Task Service Integration** for task count retrieval and task management

###  Business Features

- **Project Management:** Complete CRUD operations for projects
- **Project Status Tracking:** Real-time status updates (Open, In Progress, Completed)
- **Project Details:** Retrieve projects with task counts from Task Service
- **Manager Dashboard:** View all projects assigned to a specific manager
- **Admin Dashboard:** View all projects across the system
- **Project Completion:** Complete projects and automatically complete related tasks
- **Project Deletion:** Delete projects and automatically delete related tasks

###  Technical Excellence

- **Spring Cloud OpenFeign** for service-to-service communication
- **Eureka Client** for service discovery
- **Swagger/OpenAPI 3.0** interactive API documentation
- **Bean Validation** for input validation
- **ModelMapper** for object mapping
- **Log4j2** for advanced logging capabilities
- **Spring Boot Actuator** for health checks and monitoring

---

##  Technology Stack

### Core Framework

- **Spring Boot 2.3.4.RELEASE** - Application framework
- **Spring Web** - REST API development
- **Spring Data JPA** - Data persistence
- **Spring Security** - Security framework
- **Spring Cloud Hoxton.SR8** - Microservices framework

### Microservices & Communication

- **Spring Cloud OpenFeign** - Declarative REST client
- **Spring Cloud Netflix Eureka Client** - Service discovery
- **Spring Cloud LoadBalancer** - Client-side load balancing

### Database

- **PostgreSQL** - Primary database
- **Hibernate** - ORM framework

### Security & Authentication

- **Keycloak 18.0.0** - Identity and Access Management
- **Keycloak Spring Boot Adapter** - Keycloak integration
- **JWT** - Token-based authentication

### Documentation & Utilities

- **SpringDoc OpenAPI 3 (1.6.6)** - API documentation
- **Lombok 1.18.30** - Boilerplate code reduction
- **ModelMapper 3.1.0** - Object mapping
- **Log4j2** - Advanced logging

### Build & Development

- **Maven** - Dependency management
- **Java 11** - Programming language

---

##  Architecture

### Microservice Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway                              │
│              (Spring Cloud Gateway)                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐... (User Service)
        │                             │
┌───────▼────────┐          ┌─────────▼──────────┐
│ Project Service│          │   Task Service     │
│ (This Service) │          │                    │
└───────┬────────┘          └────────────────────┘
        │
        │ 
        │
┌───────▼────────┐
│   PostgreSQL   │
│  (project-db)  │
└────────────────┘

        ┌──────────────┐
        │   Eureka     │
        │  Discovery   │
        └──────────────┘

        ┌──────────────┐
        │   Keycloak   │
        │  Auth Server │
        └──────────────┘
```

### Service Layer Architecture

```
┌─────────────────────────────────────────────┐
│         Controller Layer                    │
│      (ProjectController)                    │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│      Service Layer (Interface)              │
│      (ProjectService)                       │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│   Service Implementation Layer              │
│   (ProjectServiceImpl)                      │
│   • Business Logic                          │
│   • Keycloak Integration                    │
│   • Task Service Communication              │
└─────────────────┬───────────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼────────┐  ┌───────▼────────┐
│   Repository   │  │  Feign Client  │
│    (JPA)       │  │  (TaskClient)  │
└───────┬────────┘  └────────────────┘
        │
┌───────▼───────┐
│  PostgreSQL   │
└───────────────┘

         Cross-Cutting Concerns

┌─────────────────────────────────────────────┐
│  • Global Exception Handling                │
│  • Security (Keycloak Integration)          │
│  • Token Propagation (Feign Interceptor)    │
│  • Entity Auditing                          │
│  • Logging (Log4j2)                         │
└─────────────────────────────────────────────┘
```

### Project Structure

```
com.cydeo
├── client                    # Feign clients
│   ├── interceptor
│   │   └── FeignClientInterceptor
│   └── TaskClient
├── config                    # Configuration classes
│   ├── KeycloakProperties
│   ├── OpenAPI3Configuration
│   └── SecurityConfig
├── controller                # REST controllers
│   └── ProjectController
├── dto                       # Data Transfer Objects
│   ├── ProjectDTO
│   ├── response
│   │   └── TaskResponse
│   └── wrapper
│       ├── ExceptionWrapper
│       ├── ResponseWrapper
│       └── ValidationExceptionWrapper
├── entity                    # JPA entities
│   ├── BaseEntity
│   └── Project
├── enums                     # Enumerations
│   └── Status
├── exception                 # Exception handling
│   ├── GlobalExceptionHandler
│   ├── ProjectAccessDeniedException
│   ├── ProjectAlreadyExistsException
│   ├── ProjectDetailsNotRetrievedException
│   ├── ProjectIsCompletedException
│   ├── ProjectNotFoundException
│   ├── TasksCanNotBeCompletedException
│   ├── TasksCanNotBeDeletedException
│   └── UserNotFoundException
├── repository                # Data repositories
│   └── ProjectRepository
├── service                   # Business logic
│   ├── KeycloakService
│   ├── ProjectService
│   └── impl
│       ├── KeycloakServiceImpl
│       └── ProjectServiceImpl
└── util                      # Utilities
    ├── MapperUtil
    └── SwaggerExamples
```

---

##  API Documentation

The API is fully documented using **Swagger/OpenAPI 3.0** with interactive documentation.

### Accessing Swagger UI

Once the application is running, navigate to:

```
http://localhost:8082/swagger-ui.html
```

### Features of API Documentation

- **Interactive Testing:** Try out API endpoints directly from the browser
- **OAuth2 Integration:** Authenticate using Keycloak within Swagger UI
- **Complete Schema Documentation:** View all request/response models
- **Role-Based Filtering:** See which endpoints are accessible to each role

---

##  Getting Started

### Prerequisites

1. **Java 11** or higher
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **PostgreSQL 12+**
   ```bash
   psql --version
   ```

4. **Keycloak Server** (Running on port 9090)
   - Download from: https://www.keycloak.org/downloads
   - Or use Docker:
   ```bash
   docker run -p 9090:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:18.0.0 start-dev
   ```

5. **Eureka Server** (Running on port 8761)
   - Service discovery server must be running
   - Other microservices in the ecosystem should be registered

6. **Task Service** (Running and registered with Eureka)
   - Required for inter-service communication
   - Must be accessible via service discovery

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd project-service-master
   ```

2. **Create PostgreSQL Database**
   ```sql
   CREATE DATABASE project-db;
   ```

3. **Configure Keycloak**
   - Access Keycloak Admin Console: `http://localhost:9090`
   - Create a new realm: `cydeo-dev`
   - Create a client: `ticketing-app`
   - Configure client settings:
     - Client Protocol: `openid-connect`
     - Access Type: `confidential`
     - Valid Redirect URIs: `http://localhost:8082/*`
   - Create client roles: `Admin`, `Manager`
   - Note the client secret for configuration

4. **Update Application Configuration**

   Edit `src/main/resources/application.yml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/project-db
       username: your_postgres_username
       password: your_postgres_password
   
   keycloak:
     realm: cydeo-dev
     auth-server-url: http://localhost:9090/auth
     resource: ticketing-app
     credentials:
       secret: your_client_secret
   
   eureka:
     client:
       service-url:
         defaultZone: http://localhost:8761/eureka
   ```

5. **Build the project**
   ```bash
   mvn clean install
   ```

6. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run the JAR file:
   ```bash
   java -jar target/project-service-0.0.1-SNAPSHOT.jar
   ```

7. **Verify Installation**
   - Application: `http://localhost:8082`
   - Swagger UI: `http://localhost:8082/swagger-ui.html`
   - Health Check: `http://localhost:8082/actuator/health`
   - Eureka Registration: Check Eureka dashboard at `http://localhost:8761`

---

##  Configuration

### Application Properties

| Property | Description | Default Value |
|----------|-------------|---------------|
| `server.port` | Application port | 8082 |
| `spring.application.name` | Service name for Eureka | project-service |
| `spring.datasource.url` | PostgreSQL connection URL | jdbc:postgresql://localhost:5432/project-db |
| `spring.jpa.hibernate.ddl-auto` | Hibernate DDL mode | create |
| `spring.jpa.show-sql` | Show SQL queries | true |
| `keycloak.realm` | Keycloak realm name | cydeo-dev |
| `keycloak.auth-server-url` | Keycloak server URL | http://localhost:9090/auth |
| `keycloak.resource` | Keycloak client ID | ticketing-app |
| `eureka.client.service-url.defaultZone` | Eureka server URL | http://localhost:8761/eureka |

### Environment-Specific Configuration

For production environments, consider:

- Using environment variables for sensitive data
- Setting `spring.jpa.hibernate.ddl-auto=validate`
- Enabling HTTPS
- Using external configuration management (Spring Cloud Config Server)
- Configuring proper Eureka instance settings for production

---

##  Security

### Authentication Flow

1. **User Authentication**
   - Users authenticate via Keycloak
   - Keycloak issues JWT access token
   - Client includes JWT in Authorization header: `Bearer <token>`

2. **Token Validation**
   - Spring Security validates JWT signature
   - Extracts user roles from token claims
   - Enforces role-based access control

3. **Inter-Service Authentication**
   - Feign Client Interceptor automatically adds JWT token to requests
   - Token propagation ensures authenticated communication with Task Service

### Role-Based Access Control

| Role | Permissions |
|------|-------------|
| **Admin** | Full access to all projects, can view all projects, count projects by manager |
| **Manager** | Can create, update, delete, and complete own projects. Can view own projects with details |

### Access Control Rules

- **Managers** can only access projects they are assigned to
- **Admins** can access all projects
- **Employees** are denied access to project endpoints
- Access is enforced at both method level (`@RolesAllowed`) and business logic level

### Secured Endpoints

```java
// Manager Only
POST   /api/v1/project/create
GET    /api/v1/project/read/manager/{projectCode}
GET    /api/v1/project/read/all/details
GET    /api/v1/project/read/all/manager
PUT    /api/v1/project/update/{projectCode}
PUT    /api/v1/project/complete/{projectCode}
DELETE /api/v1/project/delete/{projectCode}

// Admin Only
GET    /api/v1/project/read/all/admin
GET    /api/v1/project/count/manager/{assignedManager}

// Admin or Manager
GET    /api/v1/project/read/{projectCode}
GET    /api/v1/project/check/{projectCode}
```

---

## 🔄 Inter-Service Communication

### Feign Client Integration

The service communicates with **Task Service** using Spring Cloud OpenFeign:

```java
@FeignClient(name = "task-service")
public interface TaskClient {
    @GetMapping("/api/v1/task/count/project/{projectCode}")
    ResponseEntity<TaskResponse> getCountsByProject(@PathVariable String projectCode);
    
    @PutMapping("/api/v1/task/complete/project/{projectCode}")
    ResponseEntity<TaskResponse> completeByProject(@PathVariable String projectCode);
    
    @DeleteMapping("/api/v1/task/delete/project/{projectCode}")
    ResponseEntity<TaskResponse> deleteByProject(@PathVariable String projectCode);
}
```

### Service Discovery

- **Eureka Client** enables automatic service discovery
- Service registers itself with Eureka on startup
- Feign Client uses service name (`task-service`) instead of hardcoded URLs
- Load balancing is handled automatically by Spring Cloud LoadBalancer

### Token Propagation

- **FeignClientInterceptor** automatically adds JWT token to all Feign requests
- Ensures authenticated communication between services
- Token is extracted from current security context

### Communication Flow

```
Project Service Request
         │
         ├─► Extract JWT Token
         │
         ├─► FeignClientInterceptor adds token to header
         │
         ├─► Eureka resolves "task-service" to actual URL
         │
         └─► Task Service receives authenticated request
```

---

##  Database Schema

### Entity Relationship

```
┌─────────────────────────────────────┐
│           Project Entity            │
├─────────────────────────────────────┤
│ id (PK)                             │
│ projectCode (UNIQUE)                │
│ projectName                         │
│ assignedManager                     │
│ startDate                           │
│ endDate                             │
│ projectStatus (ENUM)                │
│ projectDetail                       │
│                                     │
│ (BaseEntity fields)                 │
│ insertDateTime                      │
│ lastUpdateDateTime                  │
│ insertUserId                        │
│ lastUpdateUserId                    │
│ isDeleted                           │
└─────────────────────────────────────┘
```

### Key Entities

1. **Project**: Core entity representing a project
   - Unique project code (5 characters)
   - Assigned manager (username from Keycloak)
   - Status tracking (Open, In Progress, Completed)
   - Date range (start and end dates)
   - Soft delete support

### Audit Fields (BaseEntity)

All entities inherit audit fields:
- `insertDateTime`: Record creation timestamp
- `lastUpdateDateTime`: Last modification timestamp
- `insertUserId`: Creator user ID
- `lastUpdateUserId`: Last modifier user ID
- `isDeleted`: Soft delete flag

### Status Enumeration

```java
public enum Status {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");
}
```

---

##  API Endpoints

### Project Management

| Method | Endpoint | Description | Role Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/project/create` | Create a new project | Manager |
| GET | `/api/v1/project/read/{projectCode}` | Get project by code | Admin, Manager |
| GET | `/api/v1/project/read/manager/{projectCode}` | Get manager by project code | Manager |
| GET | `/api/v1/project/read/all/details` | Get all projects with task details | Manager |
| GET | `/api/v1/project/read/all/admin` | Get all projects (admin view) | Admin |
| GET | `/api/v1/project/read/all/manager` | Get all manager's projects | Manager |
| GET | `/api/v1/project/count/manager/{assignedManager}` | Count non-completed projects by manager | Admin |
| GET | `/api/v1/project/check/{projectCode}` | Check if project exists and is not completed | Admin, Manager |
| PUT | `/api/v1/project/update/{projectCode}` | Update a project | Manager |
| PUT | `/api/v1/project/complete/{projectCode}` | Complete a project | Manager |
| DELETE | `/api/v1/project/delete/{projectCode}` | Delete a project | Manager |

### Response Format

All endpoints return a consistent response wrapper:

**Success Response:**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Project is successfully retrieved.",
  "data": { /* response data */ }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Project does not exist.",
  "httpStatus": "NOT_FOUND",
  "localDateTime": "2024-01-05T10:30:00"
}
```

**Validation Error Response:**
```json
{
  "success": false,
  "message": "Invalid Input(s)",
  "httpStatus": "BAD_REQUEST",
  "localDateTime": "2024-01-05T10:30:00",
  "errorCount": 2,
  "validationExceptions": [
    {
      "errorField": "projectCode",
      "rejectedValue": "AB",
      "reason": "Project code should include 5 characters."
    }
  ]
}
```

---

##  Microservices Ecosystem

### Architecture Overview

This service is part of a larger microservices ecosystem:

```
                    ┌─────────────────────┐
                    │   API Gateway        │
                    │ (Spring Cloud Gateway)│
                    └──────────┬───────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
┌───────▼────────┐    ┌────────▼────────┐    ┌────────▼────────┐
│  User Service  │    │ Project Service │    │  Task Service   │
│                │    │   (This Service)│    │                 │
└────────────────┘    └────────┬────────┘    └─────────────────┘
                               │
                    ┌──────────┴──────────┐
                    │                     │
            ┌───────▼────────┐    ┌───────▼────────┐
            │   PostgreSQL   │    │   PostgreSQL   │
            │  (project-db)  │    │   (task-db)    │
            └────────────────┘    └────────────────┘

                    ┌─────────────────────┐
                    │   Eureka Server     │
                    │  (Service Discovery)│
                    └─────────────────────┘

                    ┌─────────────────────┐
                    │   Keycloak Server   │
                    │  (Authentication)   │
                    └─────────────────────┘
```

### Service Dependencies

- **Eureka Server**: Required for service discovery
- **Task Service**: Required for task-related operations
  - Task count retrieval
  - Task completion when project is completed
  - Task deletion when project is deleted
- **Keycloak Server**: Required for authentication and authorization

### Service Communication Patterns

1. **Synchronous Communication**
   - REST API calls via Feign Client
   - Used for immediate data retrieval and operations

2. **Service Discovery**
   - Eureka-based service location
   - Dynamic service resolution

3. **Load Balancing**
   - Client-side load balancing via Spring Cloud LoadBalancer
   - Automatic distribution of requests

### Benefits of Microservices Architecture

✅ **Independent Scalability:** Scale project service based on project management load  
✅ **Technology Flexibility:** Use different tech stacks per service  
✅ **Fault Isolation:** Failures in one service don't affect others  
✅ **Independent Deployment:** Deploy project service without affecting other services  
✅ **Team Autonomy:** Different teams can own different services  
✅ **Database per Service:** Own PostgreSQL database for project data  

---

##  Project Status

**Current Version:** 0.0.1-SNAPSHOT  
**Status:** Active Development  
**Architecture:** Microservice (Part of Ticketing Application Ecosystem)  

### Recent Updates

- ✅ OAuth2 integration with Keycloak
- ✅ Comprehensive API documentation with Swagger
- ✅ Role-based access control implementation
- ✅ Global exception handling
- ✅ Entity auditing system
- ✅ Feign Client integration with Task Service
- ✅ Eureka service discovery integration
- ✅ Token propagation for inter-service calls
- ✅ Project-task integration (count, complete, delete)

### Upcoming Features

- 🔄 Integration tests suite
- 🔄 Docker containerization
- 🔄 CI/CD pipeline setup
- 🔄 Distributed tracing (Spring Cloud Sleuth)
- 🔄 Circuit breaker pattern (Resilience4j)
- 🔄 Event-driven communication (Kafka/RabbitMQ)
- 🔄 Caching layer implementation (Redis)

---

##  Troubleshooting

### Common Issues

1. **Service Not Registering with Eureka**
   - Verify Eureka server is running on port 8761
   - Check `eureka.client.service-url.defaultZone` configuration
   - Ensure network connectivity between services

2. **Task Service Communication Failures**
   - Verify Task Service is running and registered with Eureka
   - Check service name matches in Feign Client (`task-service`)
   - Verify JWT token propagation in FeignClientInterceptor

3. **Keycloak Authentication Issues**
   - Verify Keycloak server is running
   - Check realm and client configuration
   - Verify client secret matches configuration

4. **Database Connection Issues**
   - Verify PostgreSQL is running
   - Check database credentials
   - Ensure database `project-db` exists

### Health Checks

Access actuator endpoints for service health:
- Health: `http://localhost:8082/actuator/health`
- Info: `http://localhost:8082/actuator/info`

---
