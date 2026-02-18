package com.example.taskmgmt.infrastructure.adapter.in.rest.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
