package com.example.usermgmt.application.port.in;

import org.springframework.data.domain.Page;
import com.example.usermgmt.domain.model.User;

public interface UserServicePort {
    byte[] generateUserReport();
    Page<User> findAll(String query, Integer page, Integer size);
    User findById(Long id);
    User save(User user);
    User update(Long id, User user);
    void deleteById(Long id);
}
