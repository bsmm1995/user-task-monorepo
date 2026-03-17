package com.example.taskmgmt.infrastructure.adapter.in.rest.exception;

import com.example.common.exception.ResourceNotFoundException;

public class TaskNotFoundException extends ResourceNotFoundException {
    public TaskNotFoundException(String message) {
        super(message, "TASK_NOT_FOUND");
    }
}
