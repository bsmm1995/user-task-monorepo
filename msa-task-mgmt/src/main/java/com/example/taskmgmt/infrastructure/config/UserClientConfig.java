package com.example.taskmgmt.infrastructure.config;

import com.example.taskmgmt.infrastructure.adapter.out.client.interceptor.ExternalApiLoggingInterceptor;
import com.example.taskmgmt.infrastructure.adapter.out.client.user.api.UserManagementApi;
import com.example.taskmgmt.infrastructure.adapter.out.client.user.invoker.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class UserClientConfig {

    private final ExternalApiLoggingInterceptor externalApiLoggingInterceptor;

    @Bean
    public UserManagementApi userManagementApi(@Value("${services.user-mgmt.base-url}") String userMgmtBaseUrl) {
        var restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(externalApiLoggingInterceptor));

        var apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(userMgmtBaseUrl);

        return new UserManagementApi(apiClient);
    }
}
