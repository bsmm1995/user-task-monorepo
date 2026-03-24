package com.example.usermgmt.infrastructure.config;

import com.example.usermgmt.infrastructure.adapter.out.client.task.api.TaskManagementApi;
import com.example.usermgmt.infrastructure.adapter.out.client.task.invoker.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class TaskClientConfig {

    @Bean
    public TaskManagementApi taskManagementApi(@Value("${services.task-mgmt.base-url}") String taskMgmtBaseUrl) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        ApiClient apiClient = new ApiClient(builder, mapper, taskMgmtBaseUrl);

        return new TaskManagementApi(apiClient);
    }
}
