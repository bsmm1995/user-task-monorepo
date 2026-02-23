package com.example.usermgmt.infrastructure.adapter.out.client.task;

import com.example.usermgmt.domain.port.TaskExternalServicePort;
import com.example.usermgmt.infrastructure.adapter.out.client.task.api.TaskManagementApi;
import com.example.usermgmt.infrastructure.adapter.out.client.task.dto.GetTasksListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExternalServiceAdapter implements TaskExternalServicePort {

    private final TaskManagementApi taskManagementApi;

    @Override
    public List<TaskExternalDto> getTasksByUserId(Long userId, int page, int size) {
        log.debug("Calling task-mgmt service via generated client for userId: {}, page: {}, size: {}", userId, page, size);
        try {
            GetTasksListResponse response = taskManagementApi.getAllTasks(null, userId, page, size);
            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .map(dto -> new TaskExternalDto(
                                dto.getId(),
                                dto.getTitle(),
                                dto.getDescription(),
                                dto.getStatus(),
                                dto.getUserId(),
                                dto.getCreatedAt()
                        ))
                        .toList();
            }
        } catch (Exception e) {
            log.error("Error calling task-mgmt service for userId: {}", userId, e);
        }
        return Collections.emptyList();
    }
}
