package com.example.usermgmt.domain.port;

import com.example.usermgmt.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepositoryPort {
    Page<User> findAll(Pageable pageable);
    Page<User> search(String query, Pageable pageable);
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
}
