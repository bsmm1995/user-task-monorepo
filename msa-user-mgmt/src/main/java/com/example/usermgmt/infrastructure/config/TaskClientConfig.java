package com.example.usermgmt.infrastructure.config;

import com.example.usermgmt.infrastructure.adapter.out.client.task.api.TaskManagementApi;
import com.example.usermgmt.infrastructure.adapter.out.client.task.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TaskClientConfig {

    @Bean
    public TaskManagementApi taskManagementApi(@Value("${services.task-mgmt.base-url}") String taskMgmtBaseUrl) {
        var restTemplate = new RestTemplate();
        var apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(taskMgmtBaseUrl);

        return new TaskManagementApi(apiClient);
    }
}
