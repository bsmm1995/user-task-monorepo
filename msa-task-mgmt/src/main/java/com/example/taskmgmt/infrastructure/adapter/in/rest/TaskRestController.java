package com.example.taskmgmt.infrastructure.adapter.in.rest;

import com.example.taskmgmt.infrastructure.adapter.in.rest.api.TaskManagementApi;
import com.example.taskmgmt.infrastructure.adapter.in.rest.api.TaskManagementApiDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskRestController implements TaskManagementApi {

    private final TaskManagementApiDelegate delegate;

    @Override
    public TaskManagementApiDelegate getDelegate() {
        return delegate;
    }
}
