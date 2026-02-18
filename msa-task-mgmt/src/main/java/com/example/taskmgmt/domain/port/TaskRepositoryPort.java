package com.example.taskmgmt.domain.port;

import com.example.taskmgmt.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaskRepositoryPort {
    Page<Task> findAll(Pageable pageable);
    Page<Task> findByTitleContaining(String title, Pageable pageable);
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Optional<Task> findById(Long id);
    Task save(Task task);
    void deleteById(Long id);
}
