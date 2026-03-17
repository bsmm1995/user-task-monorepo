# Arquitectura Hexagonal (Puertos y Adaptadores)

Este proyecto implementa los principios de **Arquitectura Hexagonal**, cuyo objetivo es aislar la lógica de negocio (el núcleo) de los detalles de infraestructura (servidores HTTP, bases de datos, APIs externas).

## 🏢 Estructura de Capas

### 1. Dominio (`domain`)
Contiene los modelos de negocio puros que representan las entidades del sistema. 
- **`domain.model`**: Entidades agnósticas (ej. `User`, `Task`). 
- **Independencia**: Esta capa no depende de nada más que de sí misma. No sabe qué es Spring, JPA o HTTP.

### 2. Aplicación (`application`)
Es la capa de orquestación. Aquí es donde vive la "verdad" de qué hace el sistema.
- **`application.port.in` (Puertos de Entrada)**: Son las interfaces que el sistema ofrece al mundo exterior (ej. `TaskServicePort`). Representan los **Casos de Uso**.
- **`application.port.out` (Puertos de Salida)**: Son las interfaces que el sistema necesita para funcionar (ej. `TaskRepositoryPort`, `UserExternalServicePort`).
- **`application.usecase` (Casos de Uso)**: Son las implementaciones de los puertos de entrada. Orquestan entidades de dominio y puertos de salida.

### 3. Infraestructura (`infrastructure`)
Contiene las implementaciones técnicas que hacen que el sistema funcione.
- **`infrastructure.adapter.in.rest`**: Adaptadores REST. Se utiliza el **Patrón Delegate** de OpenAPI Generator para separar la interfaz generada de Spring de la lógica de adaptación.
    - `*Api`: Interfaz generada automáticamente.
    - `*ApiDelegate`: Interfaz de delegado generada.
    - `*DelegateImpl`: Implementación manual del delegado que orquesta la llamada a los puertos de entrada.
    - `*RestController`: Controlador que inyecta el delegado.
- **`infrastructure.adapter.out`**: Adaptadores de Salida. Implementaciones de persistencia (JPA), clientes de APIs externas, etc.
- **`infrastructure.mapper`**: Mapeadores MapStruct configurados como componentes de Spring para transformación entre DTOs, Entidades de BD y Modelos de Dominio.

---

## 🛠️ Herramientas y Patrones Clave

### 📡 OpenAPI Generator con Patrón Delegate
Se ha configurado OpenAPI Generator con `delegatePattern = true`. Esto proporciona:
1.  **Desacoplamiento total**: El código generado por la herramienta no se mezcla con el código manual.
2.  **Mantenibilidad**: Si la API cambia, solo se regenera la interfaz y el delegado; la lógica en `DelegateImpl` se ajusta según sea necesario.
3.  **Validaciones Automáticas**: Las validaciones de Jakarta Bean Validation (`@NotNull`, `@Size`, etc.) se generan en los DTOs basados en el contrato YAML, manteniendo el **Dominio Puro**.

### 💎 Dominio Puro
Las entidades de dominio en `domain.model` son POJOs limpios sin anotaciones de:
-   **Persistencia** (`@Entity`, `@Table`) -> Se usan Entidades de JPA en la infraestructura.
-   **Validación** (`@NotBlank`, `@Email`) -> Se validan en la capa de entrada (DTOs).
-   **Documentación** (`@Schema`) -> Definido en el contrato OpenAPI.

### 🚨 Gestión de Excepciones Estandarizada
Se utiliza una jerarquía de `DomainException` en un módulo común:
-   Permite lanzar excepciones de negocio desde el núcleo sin dependencias de infraestructura.
-   Un `GlobalExceptionHandler` centralizado mapea estas excepciones a respuestas HTTP estandarizadas con códigos de error legibles (ej. `USER_NOT_FOUND`).

### Flujo de una Petición (Request Flow)
1. El **Controlador REST** (`Adapter In`) recibe la petición HTTP.
2. El Controlador mapea el DTO de entrada al **Modelo de Dominio**.
3. El Controlador llama a un **Puerto de Entrada** (`TaskServicePort`).
4. El **Caso de Uso** implementa ese puerto y orquesta la lógica.
5. El Caso de Uso usa un **Puerto de Salida** (`TaskRepositoryPort`) para guardar datos.
6. El **Adaptador de Persistencia** (`Adapter Out`) implementa el puerto de salida usando JPA/Hibernate.

---

## ✅ Beneficios de este enfoque
- **Testeabilidad**: Se puede probar el `UseCase` en total aislamiento usando mocks para los `Ports`.
- **Intercambiabilidad**: Si decidimos cambiar la base de datos de PostgreSQL a MongoDB, solo cambiamos el `Adapter Out` de persistencia; el código de negocio permanece intacto.
- **Pureza**: El negocio no se ve "contaminado" por anotaciones de JPA (`@Entity`) o de JSON (`@JsonProperty`).
