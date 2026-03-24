package com.example.common.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND");
    }
}
