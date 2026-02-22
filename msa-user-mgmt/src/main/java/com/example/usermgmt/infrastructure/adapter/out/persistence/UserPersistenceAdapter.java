package com.example.usermgmt.infrastructure.adapter.out.persistence;

import com.example.usermgmt.domain.model.User;
import com.example.usermgmt.domain.port.UserRepositoryPort;
import com.example.usermgmt.infrastructure.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final JpaUserRepository userRepository;
    private static final UserEntityMapper userEntityMapper = UserEntityMapper.INSTANCE;

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> search(String query, Pageable pageable) {
        return userRepository.search(query, pageable)
                .map(userEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(userEntityMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userEntityMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return userEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.findById(id).ifPresent(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            userRepository.save(entity);
        });
    }
}
