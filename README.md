# User-Task Monorepo (Microservicios)

Proyecto demo que implementa una gestión de usuarios y tareas utilizando **Arquitectura Hexagonal**, **Spring Boot 4**, **Gradle (Kotlin DSL)** y **OpenAPI Generator**.

## 🚀 Inicio Rápido

### Requisitos Previos
- Docker y Docker Compose.
- Java 25.
- Gradle (opcional, se incluye `gradlew`).

### 1. Levantar la Base de Datos
El proyecto utiliza PostgreSQL para los microservicios.
```bash
docker-compose up -d db
```
Esto creará automáticamente las bases de datos `user_db` y `task_db` mediante el script `init-db/init.sql`.

### 2. Generar Código y Compilar
El proyecto utiliza contratos OpenAPI (`openapi.yaml`) para generar automáticamente las interfaces de API, DTOs y clientes.
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
- **Dashboard Aggregator Service (Puerto 8083):**
  ```bash
  ./gradlew :msa-dashboard:bootRun
  ```

### 4. Documentación de la API (Swagger)
Cada microservicio expone su propia interfaz de Swagger UI para pruebas interactivas:
- **Users**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Tasks**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **Dashboard**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

---

## 🛠️ Tecnologías Clave

### OpenAPI Generator (API-First)
Este proyecto adopta el enfoque **API-First**. La definición de la API se realiza en `src/main/resources/openapi.yaml`.

- **Generación de Servidor:** Se utiliza `springdoc` como proveedor de documentación para inyectar automáticamente anotaciones de Swagger en las interfaces generadas.
- **Patrón Delegate:** Se emplea `delegatePattern = true` para desacoplar el código generado de la lógica de negocio.
- **Gestión de Errores:** Se implementa una estructura de error estandarizada (`ErrorResponse`, `ErrorBody`, `ErrorDetail`) compartida conceptualmente entre todos los servicios para garantizar respuestas consistentes.

### Microservicios
1. **`msa-user-mgmt`**: Gestión de usuarios, correos electrónicos y generación de reportes en Excel (Apache POI).
2. **`msa-task-mgmt`**: Gestión de tareas asociadas a usuarios, con validación de existencia vía cliente REST generado.
3. **`msa-dashboard`**: Agregador (BFF) que consume los servicios de Usuarios y Tareas para proporcionar una vista unificada.
4. **`msa-common`**: Módulo compartido con excepciones base (`DomainException`), constantes y utilidades.

---

## 🏗️ Arquitectura
El proyecto sigue estrictamente los principios de **Arquitectura Hexagonal**. Para una explicación detallada de las capas y el flujo de datos, consulta el archivo [ARCHITECTURE.md](./ARCHITECTURE.md).
