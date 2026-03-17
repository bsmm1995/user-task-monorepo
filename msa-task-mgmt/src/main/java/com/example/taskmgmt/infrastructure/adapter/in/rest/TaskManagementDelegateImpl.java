package com.example.taskmgmt.infrastructure.adapter.in.rest;

import com.example.taskmgmt.application.port.in.TaskServicePort;
import com.example.taskmgmt.infrastructure.adapter.in.rest.api.TaskManagementApiDelegate;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.GetTaskResponse;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.GetTasksListResponse;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.PostTaskRequest;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.PutTaskRequest;
import com.example.taskmgmt.infrastructure.mapper.PaginationMapper;
import com.example.taskmgmt.infrastructure.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskManagementDelegateImpl implements TaskManagementApiDelegate {

    private final TaskServicePort taskServicePort;
    private final TaskMapper taskMapper;
    private final PaginationMapper paginationMapper;

    @Override
    public ResponseEntity<GetTasksListResponse> getAllTasks(String title, Long userId, Integer page, Integer size) {
        var tasksPage = taskServicePort.findAll(title, userId, page, size);
        GetTasksListResponse response = new GetTasksListResponse();
        response.setData(tasksPage.getContent().stream().map(taskMapper::toDto).toList());
        response.setMeta(paginationMapper.toPaginationDto(tasksPage));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetTaskResponse> getTaskById(Long id) {
        var task = taskServicePort.findById(id);
        GetTaskResponse response = new GetTaskResponse();
        response.setData(taskMapper.toDto(task));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetTaskResponse> createTask(PostTaskRequest postTaskRequest) {
        var task = taskMapper.toDomain(postTaskRequest);
        var savedTask = taskServicePort.save(task);
        GetTaskResponse response = new GetTaskResponse();
        response.setData(taskMapper.toDto(savedTask));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetTaskResponse> updateTask(Long id, PutTaskRequest putTaskRequest) {
        var taskDetails = taskMapper.toDomain(putTaskRequest);
        var updatedTask = taskServicePort.update(id, taskDetails);
        GetTaskResponse response = new GetTaskResponse();
        response.setData(taskMapper.toDto(updatedTask));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTask(Long id) {
        taskServicePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
