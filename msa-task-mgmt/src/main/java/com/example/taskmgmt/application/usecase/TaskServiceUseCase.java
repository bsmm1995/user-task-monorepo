package com.example.taskmgmt.application.usecase;

import com.example.common.exception.UserNotFoundException;
import com.example.taskmgmt.application.port.in.TaskServicePort;
import com.example.taskmgmt.domain.model.Task;
import com.example.taskmgmt.domain.port.TaskRepositoryPort;
import com.example.taskmgmt.domain.port.UserExternalServicePort;
import com.example.taskmgmt.infrastructure.adapter.in.rest.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceUseCase implements TaskServicePort {

    private final TaskRepositoryPort taskRepositoryPort;
    private final UserExternalServicePort userExternalServicePort;

    @Override
    public Page<Task> findAll(String title, Long userId, Integer page, Integer size) {
        log.debug("Starting findAll operation with title: '{}', userId: {}, page: {}, size: {}", title, userId, page, size);

        var pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        log.debug("PageRequest created: page={}, size={}, sort=createdAt:DESC", page, size);

        Page<Task> taskPage;
        boolean hasTitle = StringUtils.isNotBlank(title);
        boolean hasUserId = userId != null;

        if (hasTitle && hasUserId) {
            log.info("Searching tasks with title containing: '{}' and userId: {}", title.trim(), userId);
            taskPage = taskRepositoryPort.findByTitleAndUserId(title, userId, pageRequest);
        } else if (hasTitle) {
            log.info("Searching tasks with title containing: '{}'", title.trim());
            taskPage = taskRepositoryPort.findByTitleContaining(title, pageRequest);
        } else if (hasUserId) {
            log.info("Searching tasks for userId: {}", userId);
            taskPage = taskRepositoryPort.findByUserId(userId, pageRequest);
        } else {
            log.info("Retrieving all tasks");
            taskPage = taskRepositoryPort.findAll(pageRequest);
        }

        log.info("Operation completed. Found {} tasks out of {} total", taskPage.getNumberOfElements(), taskPage.getTotalElements());

        return taskPage;
    }

    @Override
    public Task findById(Long id) {
        log.debug("Starting findById operation for task id: {}", id);
        Task task = taskRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        log.info("Task found with id: {} - Title: '{}', Status: {}", id, task.getTitle(), task.getStatus());
        return task;
    }

    @Override
    public Task save(Task task) {
        log.debug("Starting save operation for task: '{}' - Status: {} - UserID: {}", task.getTitle(), task.getStatus(), task.getUserId());

        if (task.getUserId() != null) {
            validateUserExists(task.getUserId());
        }

        Task savedTask = taskRepositoryPort.save(task);
        log.info("Task saved successfully with id: {} - Title: '{}', Status: {}", savedTask.getId(), savedTask.getTitle(), savedTask.getStatus());
        return savedTask;
    }

    @Override
    public Task update(Long id, Task taskDetails) {
        log.debug("Starting update operation for task id: {}", id);

        Task task = taskRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found for update with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });

        if (taskDetails.getUserId() != null && !taskDetails.getUserId().equals(task.getUserId())) {
            validateUserExists(taskDetails.getUserId());
        }

        log.debug("Task found. Updating task data: title='{}' -> '{}', status='{}' -> '{}'",
                task.getTitle(), taskDetails.getTitle(), task.getStatus(), taskDetails.getStatus());
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setUserId(taskDetails.getUserId());

        Task updatedTask = taskRepositoryPort.save(task);
        log.info("Task updated successfully with id: {} - Title: '{}', Status: {}", id, updatedTask.getTitle(), updatedTask.getStatus());
        return updatedTask;
    }

    private void validateUserExists(Long userId) {
        log.debug("Validating existence of user with id: {}", userId);
        if (!userExternalServicePort.existsById(userId)) {
            throw new UserNotFoundException("Cannot assign task to non-existent user with id: " + userId);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Starting delete operation for task id: {}", id);

        taskRepositoryPort.findById(id)
                .ifPresentOrElse(
                        task -> {
                            taskRepositoryPort.deleteById(id);
                            log.info("Task deleted successfully with id: {} - Title: '{}', Status: {}", id, task.getTitle(), task.getStatus());
                        },
                        () -> log.warn("Task not found for deletion with id: {}", id)
                );
    }

}