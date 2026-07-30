# 📋 [Nombre de la Funcionalidad]

## 1. Contexto y Objetivos
- **Problema**: [¿Qué dolor resuelve?]
- **Objetivo**: [¿Cuál es el resultado esperado?]
- **Módulo(s) afectado(s)**: [msa-user-mgmt, msa-task-mgmt, msa-dashboard, msa-common]

## 2. Especificación Funcional
### Historias de Usuario / Requisitos
- [ ] **REQ-01**: [Descripción]
- [ ] **REQ-02**: [Descripción]

## 3. Diseño del Contrato (API-First)
### Cambios en `openapi.yaml`
- **Paths**: [Nuevos endpoints o cambios]
- **Modelos**: [Nuevos DTOs]

## 4. Diseño de Dominio
- **Entidades**: [Nuevos campos o entidades en `domain.model`]
- **Reglas de Negocio**: [Invariantes y validaciones de dominio]

## 5. Arquitectura Hexagonal
### Puertos de Entrada (Application - Ports In)
- `XServicePort`: [Nuevos métodos]

### Casos de Uso (Application - UseCase)
- `XServiceUseCase`: [Lógica a implementar]

### Puertos de Salida (Application - Ports Out)
- `XRepositoryPort`: [Nuevos métodos de persistencia]
- `XExternalServicePort`: [Consumo de otros servicios]

### Adaptadores (Infrastructure - Adapters)
- `XDelegateImpl`: [Mapeo DTO <-> Dominio]
- `XRepositoryAdapter`: [Implementación JPA]

## 6. Estrategia de Pruebas
- **Unitarias**: UseCase, Entidades de Dominio.
- **Integración**: Adaptadores (Repository, REST Client).
- **E2E**: Flujo completo de API.
