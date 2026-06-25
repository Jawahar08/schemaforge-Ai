# SchemaForge AI

<div align="center">

### AI-Powered Database Design Platform

Generate production-ready database schemas from natural language, collaborate with your team, export SQL for multiple databases, and manage projects through a modern AI-powered backend.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/License-MIT-green)

</div>

---

# Overview

SchemaForge AI is an enterprise-grade backend platform that converts natural language requirements into normalized relational database schemas using AI.

The platform provides complete project management, schema versioning, SQL export, team collaboration, notifications, comments, authentication, and role-based access control.

Designed using a modular Spring Boot architecture with production-ready coding practices.

---

# Features

## AI Schema Generation

- Generate database schemas from natural language
- AI-powered table and relationship generation
- Intelligent normalization
- Project-based schema management
- Schema version tracking

---

## Authentication

- JWT Authentication
- Refresh Token Support
- BCrypt Password Encryption
- Secure Spring Security Configuration
- Role-Based Authorization

---

## Project Management

- Create Projects
- Update Projects
- Delete Projects
- Archive Projects
- Project Ownership

---

## Schema Management

- Multiple Schemas per Project
- Version History
- Schema Metadata
- AI Generated Descriptions
- Ownership Validation

---

## SQL Export Engine

Generate production-ready SQL for:

- PostgreSQL
- MySQL
- Oracle
- SQL Server

Export tracking includes:

- Export history
- Export status
- Downloadable SQL
- Metadata storage

---

## Comments Module

Collaborative schema discussions

Features

- Create Comments
- Update Comments
- Delete Comments
- Thread Support
- Entity References
- Edit Tracking
- Permission Validation

---

## Teams & Collaboration

Workspace management

Features

- Create Teams
- Team Members
- Roles
- Invitations
- Workspace Ownership
- Slug Generation
- Collaboration APIs

Roles

- OWNER
- ADMIN
- MEMBER
- VIEWER

---

## Notifications

Real-time notifications

Automatically generated when:

- Schema Generated
- Export Completed
- Comment Added
- Team Invitation Created
- Invitation Accepted

Endpoints

- Get Notifications
- Get Unread
- Mark Read
- Mark All Read
- Delete Notification

---

## Security

- Spring Security
- JWT Filters
- Route Authorization
- User Ownership Validation
- Team Permission Validation

---

## Database

- PostgreSQL
- Flyway Migrations
- UUID Primary Keys
- JSONB Metadata
- Optimized Indexes
- Cascade Relationships

---

# Architecture

```
Client
   │
   ▼
Spring Boot REST API
   │
   ├── Authentication
   ├── Projects
   ├── Schemas
   ├── AI Generation
   ├── SQL Export
   ├── Teams
   ├── Comments
   ├── Notifications
   └── Security
        │
        ▼
 PostgreSQL
```

---

# Tech Stack

## Backend

- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- Hibernate
- MapStruct
- Lombok
- Validation API

## Database

- PostgreSQL
- Flyway

## AI

- Google Gemini API
- Claude API (Supported)

## Build

- Maven

## Documentation

- Swagger UI
- OpenAPI 3

---

# Project Structure

```
src/main/java/com/schemaforge

├── ai
├── auth
├── comment
├── common
├── config
├── export
├── notification
├── project
├── schema
├── security
├── team
└── user
```

---

# REST Modules

| Module | Status |
|---------|--------|
| Authentication | ✅ |
| Users | ✅ |
| Projects | ✅ |
| Schema Generation | ✅ |
| AI Integration | ✅ |
| SQL Export | ✅ |
| Comments | ✅ |
| Teams | ✅ |
| Notifications | ✅ |
| Security | ✅ |

---

# API Documentation

Swagger

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI

```
http://localhost:8080/v3/api-docs
```

---

# Running Locally

Clone

```bash
git clone https://github.com/Jawahar08/modelmind-ai.git
```

Backend

```bash
cd backend
```

Configure

```
application.yml
```

Run

```bash
mvn spring-boot:run
```

Compile

```bash
mvn clean compile
```

---

# Database Migration

Uses Flyway.

Migration scripts are located in

```
src/main/resources/db/migration
```

---

# Security Features

- BCrypt Password Hashing
- JWT Authentication
- Authorization Filters
- Owner Validation
- Team Access Validation
- Global Exception Handling

---

# Current Progress

Completed

- Authentication
- Projects
- Schema Management
- AI Generation
- SQL Export
- Comments
- Teams
- Notifications

In Progress

- Activity Feed
- Audit Logs
- Real-time Collaboration
- WebSocket Notifications

Planned

- Frontend Dashboard
- Live Collaboration
- Schema Diff Viewer
- Database Reverse Engineering
- Git Integration
- API Keys
- Billing
- Analytics

---

# Future Vision

SchemaForge AI aims to become an end-to-end collaborative database engineering platform where developers can:

- Design databases using AI
- Collaborate with teams
- Track schema history
- Export production SQL
- Manage versions
- Receive real-time notifications
- Deploy database changes

---

# Author

**Jawahar Bharathi**

Full Stack Developer

GitHub

https://github.com/Jawahar08

LinkedIn

(Add your LinkedIn URL)

---

# License

MIT License