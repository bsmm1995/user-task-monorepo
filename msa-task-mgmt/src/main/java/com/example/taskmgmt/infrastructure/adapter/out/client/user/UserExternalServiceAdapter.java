package com.example.taskmgmt.infrastructure.adapter.out.client.user;

import com.example.taskmgmt.application.port.out.UserExternalServicePort;
import com.example.taskmgmt.infrastructure.adapter.in.rest.exception.UserServiceCommunicationException;
import com.example.taskmgmt.infrastructure.adapter.out.client.user.api.UserManagementApi;
import com.example.taskmgmt.infrastructure.adapter.out.client.user.invoker.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                log.warn("User not found with id: {}", userId);
                return false;
            }
            log.error("Error communicating with User Service while checking user id: {}", userId, e);
            throw new UserServiceCommunicationException("Error validating user existence", e);
        } catch (Exception e) {
            log.error("Error communicating with User Service while checking user id: {}", userId, e);
            throw new UserServiceCommunicationException("Error validating user existence", e);
        }
    }
}
