package com.example.dashboard.infrastructure.adapter.out.client;

import com.example.dashboard.application.port.out.LoadUserPort;
import com.example.dashboard.domain.model.DashboardUser;
import com.example.dashboard.infrastructure.adapter.out.client.user.api.UserManagementApi;
import com.example.dashboard.infrastructure.adapter.out.client.user.dto.GetUserResponse;
import com.example.dashboard.infrastructure.adapter.out.client.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserClientAdapter implements LoadUserPort {

    private final UserManagementApi userApi;

    @Override
    public Optional<DashboardUser> loadUser(Long userId) {
        try {
            GetUserResponse response = userApi.getUserById(userId);
            if (response == null || response.getData() == null) {
                return Optional.empty();
            }
            UserResponse user = response.getData();
            return Optional.of(DashboardUser.builder()
                    .id(user.getId())
                    .fullName(user.getFirstName() + " " + user.getLastName())
                    .email(user.getEmail())
                    .build());
        } catch (Exception e) {
            // Handle 404 or other errors
            return Optional.empty();
        }
    }
}
