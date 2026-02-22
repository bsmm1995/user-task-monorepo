package com.example.taskmgmt.infrastructure.adapter.in.rest.exception;

public class UserServiceCommunicationException extends RuntimeException {
    public UserServiceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
