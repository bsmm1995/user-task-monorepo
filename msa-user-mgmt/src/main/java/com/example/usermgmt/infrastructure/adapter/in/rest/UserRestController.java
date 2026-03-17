package com.example.usermgmt.infrastructure.adapter.in.rest;

import com.example.usermgmt.infrastructure.adapter.in.rest.api.UserManagementApi;
import com.example.usermgmt.infrastructure.adapter.in.rest.api.UserManagementApiDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController implements UserManagementApi {

    private final UserManagementApiDelegate delegate;

    @Override
    public UserManagementApiDelegate getDelegate() {
        return delegate;
    }
}
