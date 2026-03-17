package com.example.usermgmt.application.port.out;

import java.time.OffsetDateTime;
import java.util.List;

public interface TaskExternalServicePort {
    List<TaskExternalDto> getTasksByUserId(Long userId, int page, int size);
    
    record TaskExternalDto(Long id, String title, String description, String status, Long userId, OffsetDateTime createdAt) {}
}
