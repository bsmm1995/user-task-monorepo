package com.example.taskmgmt.infrastructure.config;

import com.example.taskmgmt.infrastructure.adapter.out.client.user.api.UserManagementApi;
import com.example.taskmgmt.infrastructure.adapter.out.client.user.invoker.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class UserClientConfig {

    @Bean
    public UserManagementApi userManagementApi(@Value("${services.user-mgmt.base-url}") String userMgmtBaseUrl) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ApiClient apiClient = new ApiClient(builder, mapper, userMgmtBaseUrl);

        return new UserManagementApi(apiClient);
    }
}
