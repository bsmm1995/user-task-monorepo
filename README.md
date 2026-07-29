# User-Task Monorepo (Microservicios)

Proyecto demo que implementa una gestión de usuarios y tareas utilizando **Arquitectura Hexagonal**, **Spring Boot 4**, **Gradle (Kotlin DSL)** y un enfoque **API-First** con **OpenAPI Generator**.

## 🚀 Inicio Rápido

### Requisitos Previos
- Docker y Docker Compose.
- Java 25+ (Recomendado Java 25 o superior).
- Gradle (opcional, se incluye `gradlew`).

### 1. Levantar la Base de Datos
El proyecto utiliza PostgreSQL para los microservicios.
```bash
docker-compose up -d db
```
Esto creará automáticamente las bases de datos `user_db` y `task_db` mediante el script `init-db/init.sql`.

### 2. Generar Código y Compilar
El proyecto utiliza contratos OpenAPI (`openapi.yaml`) para generar automáticamente las interfaces de API, DTOs y clientes REST.
```bash
./gradlew build
```
Las clases generadas se ubican en cada módulo bajo `build/generated/openapi` (servidor) y `build/generated/*-client` (clientes).

### 3. Ejecutar los Microservicios
Desde la raíz, puedes iniciar los servicios de dos formas:

#### Opción A: Docker Compose (Recomendado)
Esto levantará la base de datos y todos los microservicios, configurados para comunicarse a través de `localhost` (perfil `local` activo).
```bash
docker-compose up -d
```

#### Opción B: Localmente con Gradle
Si prefieres ejecutar los servicios fuera de Docker, inicia cada uno de forma independiente:
- **User Management Service (Puerto 8081):**
  ```bash
  ./gradlew :msa-user-mgmt:bootRun --args='--spring.profiles.active=local'
  ```
- **Task Management Service (Puerto 8082):**
  ```bash
  ./gradlew :msa-task-mgmt:bootRun --args='--spring.profiles.active=local'
  ```
- **Dashboard Aggregator Service (Puerto 8083):**
  ```bash
  ./gradlew :msa-dashboard:bootRun --args='--spring.profiles.active=local'
  ```

### 4. Documentación de la API (Swagger)
Cada microservicio expone su propia interfaz de Swagger UI:
- **Users**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Tasks**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **Dashboard**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

---

## 🛠️ Tecnologías y Enfoque

### OpenAPI Generator (API-First)
Adoptamos el enfoque **API-First**. La definición de la API se realiza en `src/main/resources/openapi.yaml` antes de escribir cualquier código de controlador.

- **Productividad**: Eliminación de la creación manual de DTOs y mappers básicos.
- **Sincronización**: Los clientes (SDKs internos) se generan a partir del contrato del servidor, garantizando que siempre coincidan.
- **Patrón Delegate**: Se emplea `delegatePattern = true` para desacoplar el código generado por OpenAPI de la lógica de negocio implementada manualmente.
- **Validaciones**: Las restricciones definidas en el YAML (regEx, tamaños, obligatoriedad) se traducen automáticamente a anotaciones `@NotNull`, `@Size`, etc.

### Microservicios
1. **`msa-user-mgmt`**: Gestión de usuarios, persistencia en PostgreSQL y generación de reportes Excel. Consume el servicio de tareas para validaciones.
2. **`msa-task-mgmt`**: Gestión de tareas asociadas a usuarios. Consume el servicio de usuarios para validar la existencia del propietario.
3. **`msa-dashboard`**: BFF (Backend For Frontend) que agrega información de ambos servicios para proporcionar una vista unificada del sistema.
4. **`msa-common`**: Módulo compartido con excepciones base, constantes de log y utilidades transversales.

---

## 🏗️ Arquitectura
El proyecto sigue los principios de **Arquitectura Hexagonal**. Para más detalles sobre las capas y el flujo de datos, consulta [ARCHITECTURE.md](./ARCHITECTURE.md).

Para una propuesta detallada sobre por qué y cómo adoptamos OpenAPI, consulta el documento [PROPOSAL_OPENAPI.html](./PROPOSAL_OPENAPI.html).

---

## 🤖 Copilot Configuration

This project is configured for **GitHub Copilot** out of the box.

**Copilot will auto-load**: `.github/copilot-instructions.md`

This file contains all patterns, code templates, and rules for this Hexagonal Architecture + OpenAPI project.

**Start here**: 
- **Full guide**: [AGENTS.md](./AGENTS.md) – Architecture, workflows, best practices
- **Setup**: [DEVELOPMENT.md](./DEVELOPMENT.md) – Local environment
- **Deep dive**: [ARCHITECTURE.md](./ARCHITECTURE.md) – Technical details

## 🤝 Contributing

Read [CONTRIBUTING.md](./CONTRIBUTING.md) for:
- Development workflow
- Architecture patterns to follow
- How to run tests locally
- Commit message conventions
- PR submission guidelines

## 📋 Git Configuration

- `.gitignore` – Files to exclude from git
- `.gitattributes` – Line endings and file type settings
- `.github/PULL_REQUEST_TEMPLATE.md` – PR template
- `.github/ISSUE_TEMPLATE/` – Bug and feature templates

## 🔄 CI/CD

GitHub Actions workflows (`.github/workflows/`):
- **build.yml** – Build, test, generate OpenAPI code
- **quality.yml** – Code quality checks
- **Dependabot** – Automatic dependency updates

## 📄 License

This project is licensed under the [MIT License](./LICENSE).

---

## 📖 Documentación

| Documento | Propósito |
|-----------|-----------|
| **[AGENTS.md](./AGENTS.md)** | Guía principal para arquitectura hexagonal, patrones y desarrollo |
| **[DEVELOPMENT.md](./DEVELOPMENT.md)** | Setup del entorno de desarrollo local |
| **[ARCHITECTURE.md](./ARCHITECTURE.md)** | Detalles profundos sobre capas hexagonales y flujo de datos |
| **[PROPOSAL_OPENAPI.html](./PROPOSAL_OPENAPI.html)** | Justificación y diseño del enfoque API-First |


---

## 🔗 Referencias Externas

- **Arquitectura Hexagonal**: https://alistair.cockburn.us/hexagonal-architecture/
- **OpenAPI Spec**: https://spec.openapis.org/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **MapStruct**: https://mapstruct.org/
- **Gradle**: https://gradle.org/

