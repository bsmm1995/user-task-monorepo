package com.example.usermgmt.infrastructure.adapter.in.rest;

import com.example.usermgmt.domain.model.User;
import com.example.usermgmt.domain.port.UserServicePort;
import com.example.usermgmt.infrastructure.adapter.in.rest.api.UserManagementApi;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.*;
import com.example.usermgmt.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController implements UserManagementApi {

    private final UserServicePort userServicePort;
    private static final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public ResponseEntity<GetUsersListResponse> getAllUsers(String query, Integer page, Integer size) {
        var response = userServicePort.findAll(query, page, size);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetUserResponse> getUserById(Long id) {
        var user = userServicePort.findById(id);
        GetUserResponse response = new GetUserResponse();
        response.setData(userMapper.toDto(user));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetUserResponse> createUser(PostUserRequest userDto) {
        var user = userMapper.toDomain(userDto);
        var savedUser = userServicePort.save(user);

        GetUserResponse response = new GetUserResponse();
        response.setData(userMapper.toDto(savedUser));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetUserResponse> updateUser(Long id, PutUserRequest userDto) {
        var userDetails = userMapper.toDomain(userDto);
        var updatedUser = userServicePort.update(id, userDetails);

        GetUserResponse response = new GetUserResponse();
        response.setData(userMapper.toDto(updatedUser));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        userServicePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
