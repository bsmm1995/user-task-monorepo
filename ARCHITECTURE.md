# Arquitectura Hexagonal (Puertos y Adaptadores)

Este proyecto implementa los principios de **Arquitectura Hexagonal**, cuyo objetivo es aislar la lógica de negocio (el núcleo) de los detalles de infraestructura (servidores HTTP, bases de datos, APIs externas).

## 🏢 Estructura de Capas

### 1. Dominio (`domain`)
Contiene los modelos de negocio puros que representan las entidades del sistema. 
- **`domain.model`**: Entidades agnósticas (ej. `User`, `Task`). 
- **Independencia**: Esta capa no depende de nada más que de sí misma. No sabe qué es Spring, JPA o HTTP. Se utilizan tipos de datos estándar de Java (como `Long`, `String`, `OffsetDateTime`).

### 2. Aplicación (`application`)
Es la capa de orquestación. Aquí es donde vive la "verdad" de qué hace el sistema.
- **`application.port.in` (Puertos de Entrada)**: Son las interfaces que el sistema ofrece al mundo exterior (ej. `UserServicePort`). Representan los **Casos de Uso**.
- **`application.port.out` (Puertos de Salida)**: Son las interfaces que el sistema necesita para funcionar (ej. `UserRepositoryPort`, `TaskExternalServicePort`).
- **`application.usecase` (Casos de Uso)**: Son las implementaciones de los puertos de entrada. Orquestan entidades de dominio y puertos de salida. Esta capa es responsable de la gestión de transacciones.

### 3. Infraestructura (`infrastructure`)
Contiene las implementaciones técnicas que hacen que el sistema funcione.
- **`infrastructure.adapter.in.rest`**: Adaptadores REST. Se utiliza el **Patrón Delegate** de OpenAPI Generator para separar la interfaz generada de Spring de la lógica de adaptación.
    - `*Api`: Interfaz generada automáticamente con anotaciones de Spring MVC y Swagger.
    - `*ApiDelegate`: Interfaz de delegado que permite implementar la lógica sin tocar el controlador generado.
    - `*DelegateImpl`: Implementación manual del delegado (marcada con `@Service`) que orquesta la llamada a los puertos de entrada y mapea DTOs a Modelos de Dominio.
- **`infrastructure.adapter.out`**: Adaptadores de Salida. Implementaciones de persistencia (JPA), clientes de APIs externas (generados vía OpenAPI), etc.
- **`infrastructure.mapper`**: Mapeadores MapStruct configurados como componentes de Spring para la transformación bidireccional entre DTOs, Entidades JPA y Modelos de Dominio.

---

## 🛠️ Herramientas y Patrones Clave

### 📡 OpenAPI Generator & Swagger
Se ha configurado OpenAPI con el `documentationProvider = "springdoc"`. Esto garantiza que:
1.  **Swagger UI**: Muestre todas las operaciones, modelos y ejemplos de error de forma automática.
2.  **Sincronización**: El contrato (`openapi.yaml`) es la única fuente de verdad. El código generado incluye validaciones de Bean Validation (`@NotNull`, `@Pattern`, etc.).

### 🚨 Gestión de Excepciones Estandarizada
Se utiliza una jerarquía de `DomainException` en un módulo común (`msa-common`):
-   **Lanzamiento**: Los casos de uso lanzan excepciones de negocio (ej. `UserNotFoundException`) sin conocer detalles de HTTP.
-   **Mapeo**: Un `GlobalExceptionHandler` en la capa de infraestructura captura estas excepciones y devuelve un `ErrorResponse` estructurado con `code`, `message`, `path`, `timestamp` y `details`.

### 🔄 Flujo de una Petición (Request Flow)
1.  El **Controlador REST** (generado) recibe la petición HTTP.
2.  El **DelegateImpl** mapea el DTO de entrada al **Modelo de Dominio**.
3.  El Delegate llama a un **Puerto de Entrada** (`UseCase`).
4.  El **Caso de Uso** orquesta la lógica, validando reglas de negocio.
5.  El Caso de Uso invoca un **Puerto de Salida** (Repositorio o Cliente Externo).
6.  El **Adaptador de Salida** realiza la operación técnica (DB o API REST) y devuelve el resultado mapeado de nuevo al dominio.

---

## ✅ Beneficios de este enfoque
- **Testeabilidad**: Los casos de uso se prueban con JUnit y Mockito en total aislamiento de la infraestructura.
- **Mantenibilidad**: Los cambios en la API externa o la base de datos solo afectan a sus respectivos adaptadores.
- **Claridad**: La separación de responsabilidades facilita la navegación y evolución del código.
