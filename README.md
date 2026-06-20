# 🧠 SchemaForge Ai

<div align="center">

![Status](https://img.shields.io/badge/Status-In%20Development-orange?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?style=for-the-badge\&logo=springboot)
![Next.js](https://img.shields.io/badge/Next.js-15-black?style=for-the-badge\&logo=nextdotjs)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge\&logo=postgresql)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)

# SchemaForge Ai

### Transform Plain English Into Production-Ready Database Architectures

An AI-powered platform that converts natural language requirements into database schemas, ER diagrams, SQL scripts, normalization reports, and technical documentation.

</div>

---

## 🚀 Overview

ModelMind AI is an AI-powered database architecture platform designed to bridge the gap between business requirements and database design.

Instead of manually identifying entities, relationships, primary keys, foreign keys, constraints, and normalization rules, users can simply describe their system in natural language.

### Example Input

```text
Build an e-commerce platform where customers place orders,
products belong to categories,
sellers manage inventory,
and payments are tracked.
```

### Generated Output

* Database Tables
* Columns & Data Types
* Primary Keys
* Foreign Keys
* Entity Relationships
* SQL Scripts
* ER Diagrams
* Normalization Analysis
* Technical Documentation

---

## ✨ Features

### 🧠 AI Schema Generation

Generate complete relational database schemas from plain English descriptions.

### 🗄️ Multi-Database SQL Generation

Generate SQL scripts for:

* PostgreSQL
* MySQL
* SQL Server
* Oracle

### 📊 ER Diagram Generation

Visualize database relationships automatically using interactive ER diagrams.

### 🔍 AI Schema Review

Analyze generated schemas and identify:

* Missing Relationships
* Redundant Tables
* Naming Issues
* Design Problems
* Normalization Issues

### 📚 Documentation Generator

Generate detailed technical documentation automatically.

### 📈 Normalization Assistant

Evaluate schemas against:

* First Normal Form (1NF)
* Second Normal Form (2NF)
* Third Normal Form (3NF)
* Boyce-Codd Normal Form (BCNF)

### 🕒 Schema Versioning

Track schema changes and compare historical versions.

### 💬 AI Schema Chat

Interact with generated schemas using AI.

Example:

```text
Why was the Orders table created?

Explain all foreign key relationships.

Suggest indexing improvements.

How can this schema be optimized?
```

---

## 🏗️ System Architecture

```text
┌─────────────────────────────┐
│         Next.js 15          │
│      Frontend Client        │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│      Spring Boot API        │
│      Business Services      │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│    AI Orchestration Layer   │
│ Claude • Gemini • OpenAI    │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│ PostgreSQL • Flyway • Redis │
└─────────────────────────────┘
```

---

## 🛠️ Tech Stack

### Frontend

* Next.js 15
* TypeScript
* Tailwind CSS
* ShadCN UI
* React Query
* Zustand
* Framer Motion
* React Flow
* Mermaid.js

### Backend

* Spring Boot 3.5
* Java 21
* Spring Security
* JWT Authentication
* Maven

### Database

* PostgreSQL
* Flyway
* Redis

### AI Layer

* Claude API
* Gemini API
* OpenAI API

### DevOps

* Docker
* Docker Compose
* GitHub Actions
* Vercel
* Render
* Neon PostgreSQL

---

## 📂 Project Structure

```bash
modelmind-ai/
│
├── frontend/
│   ├── src/
│   ├── public/
│   └── package.json
│
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── application.yml
│
├── docs/
│   ├── 01-architecture.md
│   ├── 02-database-design.md
│   ├── 03-api-documentation.md
│   └── 04-deployment-guide.md
│
├── .github/
│   └── workflows/
│
├── docker-compose.yml
├── README.md
└── .gitignore
```

---

## 📊 Database Modules

Current architecture includes:

* Users
* Teams
* Team Members
* Invitations
* Projects
* Schemas
* Schema Versions
* AI Requests
* Comments
* Exports
* Notifications
* Audit Logs

---

## 🎯 Development Roadmap

### ✅ Phase 1 Completed

* System Architecture Design
* Database Design
* Flyway Migrations
* Documentation

### 🚧 Phase 2 In Progress

* Authentication Module
* JWT Security
* User Management

### 📌 Phase 3

* Project Management APIs
* Schema Management APIs

### 📌 Phase 4

* AI Integration Layer
* Schema Generation Engine

### 📌 Phase 5

* Dashboard UI
* Interactive ER Diagram Editor

### 📌 Phase 6

* SQL Export
* Documentation Export

### 📌 Phase 7

* Deployment
* Monitoring
* CI/CD Pipeline

---

## ⚡ Getting Started

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/modelmind-ai.git

cd modelmind-ai
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Database

```bash
docker-compose up -d
```

---

## 📸 Screenshots

Coming Soon

---

## 🎯 Use Cases

* Full Stack Developers
* Software Engineers
* Database Architects
* Startup Founders
* System Designers
* Students Learning DBMS

---

## 🤝 Contributing

Contributions, suggestions, and pull requests are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push your branch
5. Open a Pull Request

---

## 📜 License

MIT License

---

## 👨‍💻 Author

**Jawahar Bharathi**

Full Stack Developer | Spring Boot Developer | AI Application Builder

---

<div align="center">

⭐ Star this repository if you find it useful.

"Accelerate hire. Unlock human potential. Build the future of teams."
Made with ❤️ by Jawahar Bharathi

</div>
