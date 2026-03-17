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
- **`infrastructure.adapter.in` (Adaptadores de Entrada)**: Controladores REST que implementan las interfaces generadas por OpenAPI y delegan a los puertos de entrada.
- **`infrastructure.adapter.out` (Adaptadores de Salida)**: Implementaciones de persistencia (JPA), clientes de APIs externas (Feign/RestTemplate), etc.
- **`infrastructure.mapper`**: Se encarga de transformar datos entre DTOs de API, Entidades de BD y Modelos de Dominio.

---

## 🔌 El Concepto de "Puerto" vs "Servicio"

En este proyecto, se utiliza el sufijo **`Port`** para diferenciar claramente los contratos de sus implementaciones:

- **Puerto (`Port`)**: Es la "intención" o el contrato agnóstico. Es lo que el sistema "expone" o "necesita".
- **Caso de Uso (`UseCase`)**: Es el "cómo" se resuelve la lógica de negocio.
- **Adaptador (`Adapter`)**: Es el "cómo" se conecta el sistema con una tecnología específica.

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
