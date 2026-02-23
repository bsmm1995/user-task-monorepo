package com.example.usermgmt.domain.port;

import com.example.usermgmt.infrastructure.adapter.in.rest.dto.GetUsersListResponse;
import com.example.usermgmt.domain.model.User;

public interface UserServicePort {
    byte[] generateUserReport();
    GetUsersListResponse findAll(String query, Integer page, Integer size);
    User findById(Long id);
    User save(User user);
    User update(Long id, User user);
    void deleteById(Long id);
}
