package com.example.usermgmt.infrastructure.adapter.in.rest;

import com.example.usermgmt.application.port.in.UserServicePort;
import com.example.usermgmt.infrastructure.adapter.in.rest.api.UserManagementApiDelegate;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.GetUserResponse;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.GetUsersListResponse;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.PostUserRequest;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.PutUserRequest;
import com.example.usermgmt.infrastructure.mapper.PaginationMapper;
import com.example.usermgmt.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserManagementDelegateImpl implements UserManagementApiDelegate {

    private final UserServicePort userServicePort;
    private final UserMapper userMapper;
    private final PaginationMapper paginationMapper;

    @Override
    public ResponseEntity<Resource> generateUserReport() {
        byte[] report = userServicePort.generateUserReport();
        ByteArrayResource resource = new ByteArrayResource(report);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "users_report_" + timestamp + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @Override
    public ResponseEntity<GetUsersListResponse> getAllUsers(String query, Integer page, Integer size) {
        var userPage = userServicePort.findAll(query, page, size);
        GetUsersListResponse response = new GetUsersListResponse();
        response.setData(userPage.getContent().stream().map(userMapper::toDto).toList());
        response.setMeta(paginationMapper.toPaginationDto(userPage));
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
