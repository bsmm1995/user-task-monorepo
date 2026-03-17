package com.example.taskmgmt.application.port.in;

import com.example.taskmgmt.domain.model.Task;
import org.springframework.data.domain.Page;

public interface TaskServicePort {
    Page<Task> findAll(String title, Long userId, Integer page, Integer size);

    Task findById(Long id);

    Task save(Task task);

    Task update(Long id, Task task);

    void deleteById(Long id);
}
