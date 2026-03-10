# 🏗️ ABDE Backend

Backend microservice for the ABDE application, built with **hexagonal architecture** for maximum code quality and maintainability.

## 📋 Table of Contents

- [Architecture](#architecture)
- [Technologies](#technologies)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Usage](#usage)
- [Tests](#tests)
- [API Documentation](#api-documentation)
- [Development Workflow](#development-workflow)
- [Clear Dependencies](#clear-dependencies)
- [Before Commit Checklist](#before-commit-checklist)

---

## 🏛️ Architecture

### Hexagonal Principles (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────┐
│                    EXTERNAL SYSTEMS                          │
│  (Web Clients, Databases, External APIs, etc.)              │
└──────────────────┬──────────────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
   ┌────▼──────┐      ┌──────▼────┐
   │  INPUT    │      │  OUTPUT   │
   │ ADAPTERS  │      │ ADAPTERS  │
   │(REST API) │      │(Database) │
   └────┬──────┘      └──────┬────┘
        │                     │
        │  ┌─────────────────┤
        │  │    PORTS        │
        ▼  ▼                  ▼
   ┌──────────────────────────────────┐
   │    APPLICATION LAYER             │
   │  (Use Cases, Services, Ports)    │
   └──────────────────┬───────────────┘
                      │
   ┌──────────────────▼───────────────┐
   │      DOMAIN LAYER (CORE)         │
   │  (Business Entities, Rules)      │
   │   Pure - No frameworks!          │
   └──────────────────────────────────┘
```

### Layers in Detail

| Layer | Responsibility | Examples |
|--------|---|---|
| **Domain** | Pure business logic | Entities, Business Rules |
| **Application** | Use case orchestration | Services, UseCases, Ports |
| **Adapters (In)** | Entry points | REST Controllers, DTO |
| **Adapters (Out)** | Port implementations | Repositories, Mappers |
| **Infrastructure** | Technical details | Database configs, JPA Repositories |

---

## 🛠️ Technologies

### Main Stack

| Technology | Version | Role |
|---|---|---|
| **Java** | 25 | Language |
| **Spring Boot** | 4.0.3 | Framework |
| **PostgreSQL** | Latest | Database |
| **Maven** | 3.9+ | Build tool |
| **Flyway** | Latest | Database migrations |
| **JUnit 5** | Latest | Unit tests |
| **ArchUnit** | Latest | Architecture tests |

## 🚀 Quick Start

### Prerequisites

- ☕ Java 25+
- 🐘 PostgreSQL 18+
- 📦 Maven 3.9+
- 🐳 Docker

### Clone and Compile

```bash
git clone git@github.com:Liivasoa/abde-back.git
cd abde-back
```

### Build and start the app

```bash
# Start the app
docker-compose up --build
```

### Debug with VS Code (Dockerized)

1. **Add `.vscode/launch.json`**:
```json
{
  "version": "0.2.0",
  "configurations": [{
    "name": "Attach to Docker",
    "type": "java",
    "request": "attach",
    "hostName": "localhost",
    "port": 5005
  }]
}
```

2. **Start containers**: `docker-compose up`

3. **Debug**: Press `F5` in VS Code → "Attach to Docker"

4. **Set breakpoints**: Debug normally in editor

---

## 🧪 Tests

### Run All Tests

```bash
mvn clean test
```

### Hexagonal Architecture Tests

```bash
mvn test -Dtest="HexagonalArchitectureTest"
```

This test automatically validates:
- ✅ Domain layer doesn't depend on Spring/adapters/persistence
- ✅ Application layer doesn't directly access adapters
- ✅ Ports are interfaces (contracts)
- ✅ Use cases are interfaces
- ✅ Services implement use cases
- ✅ No circular dependencies between layers
- ✅ Naming conventions are respected (Port, Adapter, Mapper, Service, etc.)
- ✅ Ports don't use persistence frameworks

**26 tests in total organized in 8 categories** for complete hexagonal architecture coverage.

### Test Coverage

```bash
mvn clean test jacoco:report
# JaCoCo Report: target/site/jacoco/index.html
```

---

## 📚 API Documentation

### Swagger/OpenAPI

API is documented with **Swagger** and accessible via:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI Import:
```
http://localhost:8080/v3/api-docs
```

---

## 🔄 Development Workflow

### Add a New Feature

**1. Create domain entity**
```java
// src/main/java/.../domain/model/News.java
public class News {
    // Pure business logic - NO framework!
}
```

**2. Create the port (interface)**
```java
// src/main/java/.../application/port/NewsPort.java
public interface NewsPort {
    News save(News news);
}
```

**3. Create the use case**
```java
// src/main/java/.../application/usecase/CreateNewsUseCase.java
public interface CreateNewsUseCase {
    News execute(String title, String content);
}
```

**4. Implement the service**
```java
// src/main/java/.../application/service/CreateNewsService.java
@Component
public class CreateNewsService implements CreateNewsUseCase {
    private final NewsPort newsPort;
    
    @Override
    public News execute(String title, String content) {
        News news = new News(title, content);
        return newsPort.save(news);
    }
}
```

**5. Implement the adapter (repository)**
```java
// src/main/java/.../adapter/out/NewsAdapter.java
@Component
public class NewsAdapter implements NewsPort {
    private final NewsJpaRepository repo;
    
    @Override
    public News save(News news) {
        NewsEntity entity = mapper.toEntity(news);
        NewsEntity saved = repo.save(entity);
        return mapper.toDomain(saved);
    }
}
```

**6. Create the controller (REST)**
```java
// src/main/java/.../adapter/in/NewsController.java
@RestController
@RequestMapping("/news")
public class NewsController {
    private final CreateNewsUseCase useCase;
    
    @PostMapping
    public ResponseEntity<NewsResponse> create(@RequestBody NewsRequest req) {
        News news = useCase.execute(req.title(), req.content());
        return ResponseEntity.ok(mapper.toResponse(news));
    }
}
```

**7. Architecture tests pass automatically** ✅

---

## 🧩 Clear Dependencies

### Domain NEVER depends on:
- ❌ Spring
- ❌ JPA/Hibernate/Persistence
- ❌ Adapters
- ❌ Frameworks

### Application depends on:
- ✅ Domain
- ✅ Ports (interfaces)
- ✅ Spring (annotations)
- ✅ Java standard library

### Adapters depend on:
- ✅ Domain
- ✅ Ports (interfaces)
- ✅ Infrastructure/Spring
- ✅ Frameworks (JPA, REST, etc.)

---

## 📋 Before Commit Checklist

- Tests pass + coverage ok: `mvn clean verify`
- Code formatted
- Documentation updated
- Clear commit messages: [XXX][YYY] commit message (XXX -> what you did: test, dev, refactor / YYY -> what part of app: domain, adapter, ...)

---