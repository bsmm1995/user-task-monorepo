# 🚀 SPECT: Flujo de Desarrollo Guiado por IA y Especificaciones

Este documento define la metodología **SPECT** (Specification, Plan, Execution, Context, Test) utilizada en este repositorio para maximizar la productividad con asistentes de IA.

## 🔄 El Ciclo SPECT

| Fase | Archivo / Artefacto | Responsabilidad |
| :--- | :--- | :--- |
| **S**pecification | `specs/FEATURE_XXX.md` | Definir el "qué" (negocio, API, requisitos). |
| **P**lan | `SCRATCHPAD.md` (Plan) | Definir el "cómo" (pasos técnicos, cambios en código). |
| **E**xecution | Código Fuente | Implementación mínima y enfocada. |
| **C**ontext | `AGENTS.md` / `ARCHITECTURE.md` | Mantener las reglas de arquitectura y stack actualizadas. |
| **T**est | `src/test/...` | Validar que la ejecución cumple la especificación. |

---

## 🛠️ Cómo iniciar una nueva tarea

1.  **Copia la plantilla**: Usa `specs/FEATURE_TEMPLATE.md` para crear una nueva especificación en la carpeta `specs/`.
2.  **Define el Contrato**: Si hay cambios en la API, actualiza el `openapi.yaml` correspondiente primero.
3.  **Inicializa el Scratchpad**: Crea o limpia el `SCRATCHPAD.md` para que la IA sepa en qué paso está.
4.  **Referencia los Guías**: Asegúrate de que la IA ha leído `AGENTS.md` y `ARCHITECTURE.md` antes de empezar a escribir código.

## 📝 Reglas de Oro
- **Contrato Primero**: Nunca escribas lógica de controlador sin haber definido el OpenAPI.
- **Dominio Puro**: Las especificaciones de dominio no deben mencionar tecnologías (Spring, JPA).
- **Puertos Claros**: Siempre define la interfaz del puerto antes que su implementación.
- **Feedback Continuo**: Usa el `SCRATCHPAD.md` para documentar decisiones tomadas durante la ejecución que no estaban en la spec original.
