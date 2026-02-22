package com.example.taskmgmt.infrastructure.adapter.out.client.user;

import com.example.taskmgmt.domain.port.UserExternalServicePort;
import com.example.taskmgmt.infrastructure.adapter.in.rest.exception.UserServiceCommunicationException;
import com.example.taskmgmt.infrastructure.adapter.out.client.user.api.UserManagementApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserExternalServiceAdapter implements UserExternalServicePort {

    private final UserManagementApi userManagementApi;

    @Override
    public boolean existsById(Long userId) {
        log.debug("Checking if user exists with id: {}", userId);
        try {
            userManagementApi.getUserById(userId);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User not found with id: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("Error communicating with User Service while checking user id: {}", userId, e);
            throw new UserServiceCommunicationException("Error validating user existence", e);
        }
    }
}
