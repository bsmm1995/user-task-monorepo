package com.example.usermgmt.application.usecase;

import com.example.common.exception.UserNotFoundException;
import com.example.usermgmt.domain.model.User;
import com.example.usermgmt.domain.port.UserRepositoryPort;
import com.example.usermgmt.domain.port.UserServicePort;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.GetUsersListResponse;
import com.example.usermgmt.infrastructure.mapper.PaginationMapper;
import com.example.usermgmt.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceUseCase implements UserServicePort {

    private final UserRepositoryPort userRepositoryPort;
    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private static final PaginationMapper paginationMapper = PaginationMapper.INSTANCE;

    @Override
    public GetUsersListResponse findAll(String query, Integer page, Integer size) {
        log.debug("Starting findAll operation with query: '{}', page: {}, size: {}", query, page, size);

        var pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        log.debug("PageRequest created: page={}, size={}, sort=createdAt:DESC", page, size);

        Page<User> userPage;
        if (StringUtils.isNotBlank(query)) {
            log.info("Searching users with query: '{}'", query.trim());
            userPage = userRepositoryPort.search(query.trim(), pageRequest);
            log.info("Search completed. Found {} users out of {} total", userPage.getNumberOfElements(), userPage.getTotalElements());
        } else {
            log.info("Retrieving all users");
            userPage = userRepositoryPort.findAll(pageRequest);
            log.info("Retrieved {} users out of {} total", userPage.getNumberOfElements(), userPage.getTotalElements());
        }

        GetUsersListResponse response = buildPaginatedResponse(userPage);
        log.debug("Paginated response built successfully with {} users", response.getData().size());
        return response;
    }

    @Override
    public User findById(Long id) {
        log.debug("Starting findById operation for user id: {}", id);
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        log.info("User found with id: {} - Name: {} {}", id, user.getFirstName(), user.getLastName());
        return user;
    }

    @Override
    public User save(User user) {
        log.debug("Starting save operation for user: {} {}", user.getFirstName(), user.getLastName());
        User savedUser = userRepositoryPort.save(user);
        log.info("User saved successfully with id: {} - Name: {} {}", savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName());
        return savedUser;
    }

    @Override
    public User update(Long id, User userDetails) {
        log.debug("Starting update operation for user id: {}", id);

        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        log.debug("User found. Updating user data: {} -> {}", user.getEmail(), userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());

        User updatedUser = userRepositoryPort.save(user);
        log.info("User updated successfully with id: {} - New data: {} {}", id, updatedUser.getFirstName(), updatedUser.getLastName());
        return updatedUser;
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Starting delete operation for user id: {}", id);

        userRepositoryPort.findById(id)
                .ifPresentOrElse(
                        user -> {
                            userRepositoryPort.deleteById(id);
                            log.info("User deleted successfully with id: {} - Name: {} {}", id, user.getFirstName(), user.getLastName());
                        },
                        () -> log.warn("User not found for deletion with id: {}", id)
                );
    }

    private GetUsersListResponse buildPaginatedResponse(Page<User> userPage) {
        log.debug("Building paginated response. Total elements: {}, Total pages: {}", userPage.getTotalElements(), userPage.getTotalPages());

        var response = new GetUsersListResponse();
        var userDtos = userPage.getContent().stream()
                .map(userMapper::toDto)
                .toList();

        response.setData(userDtos);
        log.debug("Mapped {} users to DTOs", userDtos.size());

        var pagination = paginationMapper.toPaginationDto(userPage);
        response.setMeta(pagination);
        log.debug("Pagination metadata added: page={}, size={}, totalElements={}, totalPages={}",
                pagination.getPage(), pagination.getSize(), pagination.getTotalElements(), pagination.getTotalPages());

        return response;
    }
}
