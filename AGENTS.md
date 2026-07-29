# AGENTS.md: Guide for AI-Assisted Development

This monorepo implements **Hexagonal Architecture (Ports & Adapters)** with **API-First Design** using OpenAPI Generator. Understanding this structure is critical for productive development.

## 🏗️ Architecture Overview (The "Big Picture")

### Why Hexagonal Architecture?
The architecture isolates **business logic (domain core)** from **infrastructure details** (databases, HTTP, external APIs). This separation enables:
- **Testability**: Domain and use cases are pure business logic, easily unit tested with mocks
- **Independence**: Changing PostgreSQL to MongoDB only affects the Repository adapter, not business rules
- **Clarity**: The flow of data and control is explicitly defined through ports

### Critical Design Decision: API-First with delegatePattern
The project uses OpenAPI Generator with `delegatePattern = true` to **automatically generate** REST controllers and DTOs. Then **you implement** `*DelegateImpl` classes to map between HTTP/DTOs and domain models. This avoids manual boilerplate and keeps generated code and business logic decoupled.

### Layered Request Flow (Essential Pattern)
1. HTTP request → **Generated API controller** (auto-wired to delegate)
2. **DelegateImpl** (manual): maps DTO → domain model, calls input port
3. **UseCase** (manual): orchestrates domain entities and invokes output ports
4. **Output Adapter** (manual): implements repository/external service port
5. Response flows back through mappers to DTO

**Example Path**: `msa-user-mgmt/src/main/java/com/example/usermgmt/`
- `domain/model/User.java` - Pure business entity
- `application/port/in/UserServicePort.java` - "What can we do?"
- `application/usecase/UserServiceUseCase.java` - "How do we do it?"
- `infrastructure/adapter/in/rest/UserManagementDelegateImpl.java` - "How does HTTP talk to us?"
- `infrastructure/adapter/out/UserRepositoryAdapter.java` - "How do we persist?"
- `infrastructure/mapper/UserMapper.java` - DTO ↔ Domain translation

---

## 🔧 Critical Developer Workflows

### Build & Code Generation
```bash
# Full build (compiles and generates OpenAPI code)
./gradlew build

# For faster iteration, regenerate code without full rebuild
./gradlew :msa-user-mgmt:openApiGenerateUser :msa-user-mgmt:openApiGenerateTaskClient
```
**Key Point**: Generated code lives in `build/generated/openapi/` and `build/generated/*-client/`. Must regenerate after editing `openapi.yaml`.

### Run Services Locally
```bash
# Terminal 1: Database
docker-compose up -d db

# Terminal 2: User Service (port 8081)
./gradlew :msa-user-mgmt:bootRun --args='--spring.profiles.active=local'

# Terminal 3: Task Service (port 8082)
./gradlew :msa-task-mgmt:bootRun --args='--spring.profiles.active=local'

# Terminal 4: Dashboard Service (port 8083)
./gradlew :msa-dashboard:bootRun --args='--spring.profiles.active=local'
```

### Viewing API Documentation
Each service exposes **Swagger UI** (auto-generated from OpenAPI):
- User Service: http://localhost:8081/swagger-ui.html
- Task Service: http://localhost:8082/swagger-ui.html
- Dashboard: http://localhost:8083/swagger-ui.html

---

## 📋 Project-Specific Conventions (Not Standard Spring)

### 1. Input Ports (`application.port.in.*`)
- Interface, not class (name: `*ServicePort`, `*UseCase`)
- Defines the external boundary of use cases
- No @Service annotation (interface only)
- Example: `UserServicePort` declares "createUser", "getUserById", etc.

### 2. Output Ports (`application.port.out.*`)
- Interface for dependencies (repositories, external services)
- Name: `*RepositoryPort`, `*ExternalServicePort`
- Domain models only in signatures (no DTOs)
- Example: `UserRepositoryPort.save(User)` returns `User` (domain), not a DTO

### 3. UseCase Implementations
- Class marked with `@Service`
- Implements input port interface
- **Always** `@Transactional` (handles transaction boundaries)
- Injects output ports via constructor (dependency injection)
- Throws **domain exceptions**, not HTTP exceptions (e.g., `UserNotFoundException`, not 404)
- Example path: `application/usecase/UserServiceUseCase.java`

### 4. DelegateImpl Pattern (REST Adapters)
- File name: `*DelegateImpl` (e.g., `UserManagementDelegateImpl`)
- Marked with `@Service`
- Implements generated `*ApiDelegate` interface
- **Responsibilities**:
  - Map DTO → domain model
  - Call input port (usecase)
  - Build response DTOs
  - **Never** contain business logic
- Example: Instead of `UserManagementApi`, implement `UserManagementApiDelegate`

### 5. MapStruct Mappers
- Interface annotated with `@Mapper(componentModel = "spring")`
- Mapped from `jakarta.persistence` or domain → generated DTOs
- Use `@Mapping(target = "...", ignore = true)` for fields NOT in DTO (e.g., database system fields)
- **Ignore patterns**: `id`, `createdAt`, `updatedAt` when mapping TO DTOs (these come from OpenAPI generation)
- Example: `UserMapper.toDomain(UserDto dto)` and `UserMapper.toDto(User domain)`

### 6. Exception Hierarchy (Centralized in msa-common)
- Base: `DomainException(errorCode, message)` in `msa-common`
- Domain exceptions: `UserNotFoundException extends DomainException`, `TaskNotFoundException`, etc.
- **GlobalExceptionHandler** in each service catches `DomainException` and returns JSON `ErrorResponse{code, message, path, timestamp, details}`
- Business logic throws domain exceptions; HTTP status codes are mapped in the handler
- Example: Usecase throws `UserNotFoundException` → adapter catches it → returns 404 JSON

### 7. Output Ports in Action
- Adapters implement these ports to connect to databases, external services
- Name: `*RepositoryAdapter`, `*ExternalServiceAdapter`
- Maps domain models ← → persistence/external representations
- Located in `infrastructure/adapter/out/`

---

## 🔌 Cross-Service Communication

### Internal Service-to-Service Calls
Services consume each other via **OpenAPI-generated clients**:
- `msa-user-mgmt` calls `msa-task-mgmt` through generated client in `build/generated/task-client/`
- Client generated from Task service's `openapi.yaml`
- Adapter wraps client, implements output port
- UseCase injects adapter and calls methods (no manual HTTP boilerplate)

### Example (msa-user-mgmt calling msa-task-mgmt)
- **Output Port**: `application/port/out/TaskExternalServicePort.java`
- **Adapter**: `infrastructure/adapter/out/TaskServiceAdapter.java` (implements port, uses generated client)
- **UseCase**: `application/usecase/UserServiceUseCase.java` (calls `taskExternalServicePort.getTasksByUserId(...)`)

---

## 🛠️ Common Modification Tasks

### Adding a New API Endpoint
1. Edit `src/main/resources/openapi.yaml` (add path, parameters, response schema)
2. Run `./gradlew :msa-user-mgmt:openApiGenerateUser` to regenerate controller
3. Implement new `DelegateImpl` method (maps DTO → domain, calls usecase)
4. Add method to input port interface (application.port.in)
5. Implement in usecase class
6. Write unit tests for usecase logic

### Adding New Persistence Logic
1. Add JPA entity in `infrastructure/adapter/out/persistence/`
2. Create JPA repository interface extending `JpaRepository`
3. Create adapter class implementing output port (maps domain ↔ entity)
4. Inject adapter in usecase

### Calling External Microservice
1. Define input port in the called service's `openapi.yaml`
2. Update calling service's build.gradle to generate client from other service's YAML
3. Create output port in calling service (`*ExternalServicePort`)
4. Implement adapter wrapping the generated client
5. Inject into usecase and call (domain models only)

---

## 📊 Module Responsibilities

| Module | Purpose | Key Files |
|--------|---------|-----------|
| **msa-common** | Shared utilities, exception hierarchy, constants | `exception/DomainException.java`, logging constants |
| **msa-user-mgmt** | User CRUD, reporting (Excel export) | `domain/model/User.java`, `UserServicePort`, `UserRepositoryPort` |
| **msa-task-mgmt** | Task CRUD, validates user existence | `domain/model/Task.java`, calls UserExternalServicePort |
| **msa-dashboard** | BFF aggregating user + task data | Calls both UserServicePort and TaskServicePort |

---

## 🚀 Performance & Debugging Tips

- **Generated code is immutable**: Don't edit files in `build/generated/`. Regenerate from YAML.
- **Transactional boundaries**: @Transactional on usecase methods, not on adapters
- **Lazy loading issues**: JPA relationships (User → Tasks) are LAZY by default; call them inside @Transactional
- **Cross-service calls fail gracefully**: Wrap external service calls in try-catch with fallback logic
- **Profile-specific config**: `application-local.yml`, `application-dev.yml`, `application-prod.yml` in resources/

---

## 🔗 Reference Files

- **Architecture deep-dive**: `ARCHITECTURE.md` (read for complete layer explanations)
- **OpenAPI rationale**: `PROPOSAL_OPENAPI.html`
- **Build configuration**: `build.gradle.kts` (root) and module-specific build files
- **Service contracts**: `src/main/resources/openapi.yaml` in each service
- **Communication diagram**: `communication_diagram.png`

---

## ⚡ Quick Checklist Before Making Changes

- [ ] Is this a domain model change? (affects business logic)
- [ ] Is this an API contract change? (edit openapi.yaml, regenerate)
- [ ] Is this a persistence change? (affects repository adapter)
- [ ] Is this a cross-service call? (check external service port)
- [ ] Will this affect transaction boundaries? (check @Transactional placement)
- [ ] Run `./gradlew build` after any YAML changes or dependency updates

