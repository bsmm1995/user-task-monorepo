package com.example.dashboard.infrastructure.config;

import com.example.dashboard.infrastructure.adapter.out.client.task.api.TaskManagementApi;
import com.example.dashboard.infrastructure.adapter.out.client.user.api.UserManagementApi;
import com.example.dashboard.infrastructure.adapter.out.client.user.invoker.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ClientConfiguration {

    @Bean
    public UserManagementApi userManagementApi(@Value("${services.user-mgmt.base-url}") String userMgmtBaseUrl) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5));

        ObjectMapper mapper = ApiClient.createDefaultObjectMapper();
        ApiClient apiClient = new ApiClient(builder, mapper, userMgmtBaseUrl);

        return new UserManagementApi(apiClient);
    }

    @Bean
    public TaskManagementApi taskManagementApi(@Value("${services.task-mgmt.base-url}") String taskMgmtBaseUrl) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5));

        ObjectMapper mapper = com.example.dashboard.infrastructure.adapter.out.client.task.invoker.ApiClient.createDefaultObjectMapper();
        com.example.dashboard.infrastructure.adapter.out.client.task.invoker.ApiClient apiClient =
                new com.example.dashboard.infrastructure.adapter.out.client.task.invoker.ApiClient(
                        builder,
                        mapper,
                        taskMgmtBaseUrl
                );

        return new TaskManagementApi(apiClient);
    }
}
