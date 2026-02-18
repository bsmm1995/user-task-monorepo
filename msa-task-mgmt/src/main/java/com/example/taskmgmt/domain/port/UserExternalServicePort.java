package com.example.taskmgmt.domain.port;

public interface UserExternalServicePort {
    boolean existsById(Long userId);
}
