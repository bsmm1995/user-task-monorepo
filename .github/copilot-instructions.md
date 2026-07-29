# GitHub Copilot Instructions

Instrucciones específicas para GitHub Copilot en este proyecto Hexagonal Architecture + OpenAPI Generator.

## Arquitectura (El "Big Picture")

Este es un **monorepo con Arquitectura Hexagonal** (Ports & Adapters) y **API-First Design (OpenAPI Generator)**.

```
domain/model/              → Entidades de negocio puras (sin dependencias)
application/port/          → Puertos de entrada (use cases) y salida (dependencies)
application/usecase/       → Implementaciones @Service @Transactional
infrastructure/adapter/    → Adaptadores REST (DelegateImpl) y persistencia
infrastructure/mapper/     → MapStruct mappers (DTO ↔ Domain)
```

### Flujo de una Petición
```
HTTP Request
    ↓
Generated API Controller (openapi.yaml → build/generated/openapi/)
    ↓
*DelegateImpl (mapea DTO → domain model)
    ↓
*UseCase (orchestrates business logic)
    ↓
Output Port Adapter (database, external APIs)
    ↓
Response (domain → DTO → HTTP)
```

## Reglas Críticas para Copilot

### ✅ DO: Sigue estos patrones

**1. Domain Models** - Puras, sin Spring/JPA
```java
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    // SIN @Entity, @Table, etc.
}
```

**2. Input Ports** - Interfaces en `application.port.in`
```java
public interface UserServicePort {
    User createUser(String name, String email);
    User getUserById(Long id);
}
```

**3. Output Ports** - Interfaces en `application.port.out` (domain models only)
```java
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
}
```

**4. UseCase** - @Service @Transactional, constructor injection
```java
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceUseCase implements UserServicePort {
    private final UserRepositoryPort userRepository;
    private final ExternalServicePort externalService;

    @Override
    public User createUser(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new InvalidUserException("Name required");  // Domain exception
        }
        User user = User.builder().name(name).email(email).build();
        return userRepository.save(user);
    }
}
```

**5. DelegateImpl** - REST Adapter (maps DTO ↔ Domain)
```java
@Service
@RequiredArgsConstructor
public class UserManagementDelegateImpl implements UserManagementApiDelegate {
    private final UserServicePort userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserDto> createUser(CreateUserRequest request) {
        User domain = userMapper.toDomain(request);
        User created = userService.createUser(domain.getName(), domain.getEmail());
        return ResponseEntity.ok(userMapper.toDto(created));
    }
}
```

**6. MapStruct Mapper** - @Mapper(componentModel = "spring")
```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toDomain(UserDto dto);

    UserDto toDto(User user);
}
```

**7. Repository Adapter** - Output Port Implementation
```java
@Service
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.from(user);
        return jpaRepository.save(entity).toDomain();
    }
}
```

**8. Exception Hierarchy** - Domain exceptions
```java
// Base exception
public abstract class DomainException extends RuntimeException {
    private final String errorCode;
    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

// Specific exception
public class UserNotFoundException extends DomainException {
    public UserNotFoundException(Long id) {
        super("USER_NOT_FOUND", "User not found: " + id);
    }
}

// Global handler (catches domain exceptions, returns HTTP)
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(
            UserNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), req.getRequestURI()));
    }
}
```

### ❌ DON'T: Evita estos antipatrones

1. **❌ NO mezcles capas**: Business logic en DelegateImpl, persistencia en UseCase
2. **❌ NO uses DTOs en domain**: `User` ≠ `UserDto`
3. **❌ NO edites `build/generated/`**: Regenera desde `openapi.yaml`
4. **❌ NO @Transactional en adapters**: Solo en UseCase
5. **❌ NO HTTP exceptions en business**: Lanza `DomainException`
6. **❌ NO field injection**: Usa constructor injection
7. **❌ NO business logic en mappers**: Solo transforman datos

## Generación de Código (OpenAPI)

Cuando modifiques `openapi.yaml`:
```bash
# Regenera código
./gradlew :msa-user-mgmt:openApiGenerateUser :msa-user-mgmt:openApiGenerateTaskClient

# Implementa *DelegateImpl, ports, usecases, adapters
# Prueba con Swagger UI: http://localhost:8081/swagger-ui.html
```

**CRÍTICO**: Los archivos en `build/generated/` son auto-generados. NO los edites; regenera desde YAML.

## Cross-Service Communication

Service A → Service B:

```java
// 1. Output Port (domain models only)
public interface TaskExternalServicePort {
    List<Task> getTasksByUserId(Long userId);
}

// 2. Adapter (wraps generated client)
@Service @RequiredArgsConstructor
public class TaskServiceAdapter implements TaskExternalServicePort {
    private final TasksApi tasksApi;  // Generated client
    public List<Task> getTasksByUserId(Long userId) {
        try {
            return tasksApi.getTasksByUserId(userId)
                .stream().map(this::toDomain).toList();
        } catch (Exception e) {
            throw new ExternalServiceException("Task service failed", e);
        }
    }
}

// 3. UseCase (injects port)
@Service @Transactional
public class UserServiceUseCase {
    private final TaskExternalServicePort taskPort;
    public void validate(Long userId) {
        List<Task> tasks = taskPort.getTasksByUserId(userId);
        // Business logic...
    }
}
```

## Transaction Management

- **@Transactional on UseCase ONLY** (not adapters, not controllers)
- All DB operations within same transaction
- External calls wrapped inside @Transactional

## Testing

**Unit Test** (UseCase + mocks):
```java
@ExtendWith(MockitoExtension.class)
class UserServiceUseCaseTest {
    @Mock private UserRepositoryPort repository;
    @InjectMocks private UserServiceUseCase useCase;

    @Test
    void should_create_user() {
        when(repository.save(any())).thenReturn(new User(1L, "John", "john@email.com"));
        User result = useCase.createUser("John", "john@email.com");
        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).save(any());
    }
}
```

## Build Commands

```bash
./gradlew build                    # Full build + generate code
./gradlew :msa-user-mgmt:bootRun --args='--spring.profiles.active=local'
docker-compose up -d db            # Start database
```

## Services & Modules

| Service | Port | Purpose |
|---------|------|---------|
| msa-user-mgmt | 8081 | User CRUD + reporting |
| msa-task-mgmt | 8082 | Task CRUD |
| msa-dashboard | 8083 | BFF aggregator |
| msa-common | - | Shared (exceptions, utils) |

## API Swagger UI

- User: http://localhost:8081/swagger-ui.html
- Task: http://localhost:8082/swagger-ui.html
- Dashboard: http://localhost:8083/swagger-ui.html

## When Copilot Suggests Code

1. **Verify layer separation**: Is it domain/app/infra?
2. **Check @Transactional**: Only on UseCase?
3. **Check exceptions**: Extends DomainException?
4. **Check mappers**: Uses MapStruct?
5. **Check generation**: Needs OpenAPI regenerate?

---

**Reference**:
- Full guide: `AGENTS.md`
- Setup: `DEVELOPMENT.md`
- Architecture details: `ARCHITECTURE.md`

