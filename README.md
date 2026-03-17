# User-Task Monorepo (Microservicios)

Proyecto demo que implementa una gestión de usuarios y tareas utilizando **Arquitectura Hexagonal**, **Spring Boot 4**, **Gradle (Kotlin DSL)** y **OpenAPI Generator**.

## 🚀 Inicio Rápido

### Requisitos Previos
- Docker y Docker Compose.
- Java 25.
- Gradle (opcional, se incluye `gradlew`).

### 1. Levantar la Base de Datos
El proyecto utiliza PostgreSQL para ambos microservicios.
```bash
docker-compose up -d db
```
Esto creará automáticamente las bases de datos `user_db` y `task_db` mediante el script `init-db/init.sql`.

### 2. Generar Código y Compilar
El proyecto utiliza contratos OpenAPI (`openapi.yaml`) para generar automáticamente las interfaces de API y los DTOs.
```bash
./gradlew build
```
Las clases generadas se ubican en cada módulo bajo `build/generated/openapi`.

### 3. Ejecutar los Microservicios
Desde la raíz, puedes iniciar cada servicio de forma independiente:
- **User Management Service (Puerto 8081):**
  ```bash
  ./gradlew :msa-user-mgmt:bootRun
  ```
- **Task Management Service (Puerto 8082):**
  ```bash
  ./gradlew :msa-task-mgmt:bootRun
  ```

---

## 🛠️ Tecnologías Clave

### OpenAPI Generator
Este proyecto adopta el enfoque **API-First**. La definición de la API se realiza en `src/main/resources/openapi.yaml`.

- **Generación de Servidor:** Se generan interfaces `UserManagementApi` y `TaskManagementApi` que los controladores implementan.
- **Generación de Clientes:** El microservicio de Tareas utiliza OpenAPI para generar un cliente que se comunica con el servicio de Usuarios.
- **Plantillas Personalizadas:** Se utilizan plantillas `.mustache` en `src/main/resources/openapi-templates` para:
    - Incluir respuestas de error estándar en Swagger UI.
    - Implementaciones `default` para facilitar el testing/mocking.
    - Traducir mensajes base.

### Microservicios
1. **`msa-user-mgmt`**: Gestión de usuarios, correos electrónicos y generación de reportes en Excel (Apache POI).
2. **`msa-task-mgmt`**: Gestión de tareas asociadas a usuarios, validación de existencia de usuarios vía API externa.
3. **`msa-common`**: Excepciones, constantes y utilidades compartidas.

---

## 🏗️ Arquitectura
Para más detalles sobre la implementación de la Arquitectura Hexagonal, consulta el archivo [ARCHITECTURE.md](./ARCHITECTURE.md).
