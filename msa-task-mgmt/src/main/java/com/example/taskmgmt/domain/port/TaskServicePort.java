package com.example.taskmgmt.domain.port;

import com.example.taskmgmt.domain.model.Task;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.GetTasksListResponse;

public interface TaskServicePort {
    GetTasksListResponse findAll(String title, Long userId, Integer page, Integer size);

    Task findById(Long id);

    Task save(Task task);

    Task update(Long id, Task task);

    void deleteById(Long id);
}
