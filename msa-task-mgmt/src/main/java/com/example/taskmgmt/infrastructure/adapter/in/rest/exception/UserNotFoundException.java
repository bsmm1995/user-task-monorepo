package com.example.taskmgmt.infrastructure.adapter.in.rest.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
