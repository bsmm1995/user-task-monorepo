package com.example.dashboard.infrastructure.adapter.out.client;

import com.example.dashboard.application.port.out.LoadTasksPort;
import com.example.dashboard.domain.model.DashboardTask;
import com.example.dashboard.infrastructure.adapter.out.client.task.api.TaskManagementApi;
import com.example.dashboard.infrastructure.adapter.out.client.task.dto.GetTasksListResponse;
import com.example.dashboard.infrastructure.adapter.out.client.task.dto.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskClientAdapter implements LoadTasksPort {

    private final TaskManagementApi taskApi;

    @Override
    public List<DashboardTask> loadTasks(Long userId) {
        try {
            // Fetch all tasks for user, handle pagination if needed. 
            // For now, get first page with large size.
            GetTasksListResponse response = taskApi.getAllTasks("", userId, 0, 100);
            
            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }

            return response.getData().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log error and return empty list (circuit breaker pattern simplified)
            return Collections.emptyList();
        }
    }

    private DashboardTask toDomain(TaskResponse task) {
        return DashboardTask.builder()
                .id(task.getId())
                .title(task.getTitle())
                .status(task.getStatus())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
