package com.example.taskmgmt.infrastructure.config;

import com.example.taskmgmt.infrastructure.adapter.out.client.user.api.UserManagementApi;
import com.example.taskmgmt.infrastructure.adapter.out.client.user.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserClientConfig {

    @Value("${services.user-mgmt.base-url}")
    private String userMgmtBaseUrl;

    @Bean
    public ApiClient userApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(userMgmtBaseUrl);
        return apiClient;
    }

    @Bean
    public UserManagementApi userManagementApi(ApiClient userApiClient) {
        return new UserManagementApi(userApiClient);
    }
}
