package com.example.taskmgmt.infrastructure.adapter.in.rest;

import com.example.taskmgmt.domain.port.TaskServicePort;
import com.example.taskmgmt.infrastructure.adapter.in.rest.api.TaskManagementApi;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.GetTaskResponse;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.GetTasksListResponse;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.PostTaskRequest;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.PutTaskRequest;
import com.example.taskmgmt.infrastructure.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskRestController implements TaskManagementApi {

    private final TaskServicePort taskServicePort;
    private static final TaskMapper taskMapper = TaskMapper.INSTANCE;

    @Override
    public ResponseEntity<GetTasksListResponse> getAllTasks(String title, Long userId, Integer page, Integer size) {
        var response = taskServicePort.findAll(title, userId, page, size);
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
    public ResponseEntity<GetTaskResponse> createTask(PostTaskRequest taskDto) {
        var task = taskMapper.toDomain(taskDto);
        var savedTask = taskServicePort.save(task);

        GetTaskResponse response = new GetTaskResponse();
        response.setData(taskMapper.toDto(savedTask));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetTaskResponse> updateTask(Long id, PutTaskRequest taskDto) {
        var taskDetails = taskMapper.toDomain(taskDto);
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
