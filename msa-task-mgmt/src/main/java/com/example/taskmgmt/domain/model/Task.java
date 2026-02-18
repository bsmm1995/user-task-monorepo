package com.example.taskmgmt.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task Domain Model")
public class Task {
    @Schema(description = "Unique identifier of the task", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s.,!?-]+$", message = "El título contiene caracteres no permitidos")
    @Schema(description = "Title of the task", example = "Complete the project")
    private String title;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s.,!?-]*$", message = "La descripción contiene caracteres no permitidos")
    @Schema(description = "Description of the task", example = "Finish all the pending items in the backlog")
    private String description;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
    @Pattern(regexp = "^[A-Z_]+$", message = "El estado debe estar en mayúsculas y solo contener letras o guiones bajos")
    @Schema(description = "Current status of the task", example = "PENDING")
    private String status;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "Identifier of the user who owns the task", example = "1")
    private Long userId;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private OffsetDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T15:45:30", accessMode = Schema.AccessMode.READ_ONLY)
    private OffsetDateTime updatedAt;
}
