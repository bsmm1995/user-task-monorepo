package com.example.taskmgmt.infrastructure.config;

import com.example.taskmgmt.infrastructure.adapter.out.client.user.api.UserManagementApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserClientConfig {
    @Bean
    public UserManagementApi userManagementApi(@Value("${services.user-mgmt.base-url}") String userMgmtBaseUrl) {
        var userManagementApi = new UserManagementApi();
        userManagementApi.getApiClient().setBasePath(userMgmtBaseUrl);
        return userManagementApi;
    }
}
