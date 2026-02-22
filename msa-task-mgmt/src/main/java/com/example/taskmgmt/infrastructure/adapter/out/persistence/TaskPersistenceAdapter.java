package com.example.taskmgmt.infrastructure.adapter.out.persistence;

import com.example.taskmgmt.domain.model.Task;
import com.example.taskmgmt.domain.port.TaskRepositoryPort;
import com.example.taskmgmt.infrastructure.mapper.TaskEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final JpaTaskRepository taskRepository;
    private static final TaskEntityMapper taskEntityMapper = TaskEntityMapper.INSTANCE;

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findByTitleContaining(String title, Pageable pageable) {
        return taskRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(taskEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findByUserId(Long userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable)
                .map(taskEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id).map(taskEntityMapper::toDomain);
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = taskEntityMapper.toEntity(task);
        TaskEntity savedEntity = taskRepository.save(entity);
        return taskEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        taskRepository.findById(id).ifPresent(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            taskRepository.save(entity);
        });
    }
}
