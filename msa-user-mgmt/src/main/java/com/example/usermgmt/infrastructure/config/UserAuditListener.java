package com.example.usermgmt.infrastructure.config;

import com.example.usermgmt.infrastructure.adapter.out.persistence.UserEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class UserAuditListener {
    @PrePersist
    public void prePersist(UserEntity entity) {
        String currentUser = getCurrentUsername();
        entity.setCreatedBy(currentUser);
        entity.setUpdatedBy(currentUser);
        log.debug("User audit: New user created by: {}", currentUser);
    }

    @PreUpdate
    public void preUpdate(UserEntity entity) {
        String currentUser = getCurrentUsername();
        entity.setUpdatedBy(currentUser);
        log.debug("User audit: User {} updated by: {}", entity.getId(), currentUser);
    }

    @PreRemove
    public void preRemove(UserEntity entity) {
        String currentUser = getCurrentUsername();
        entity.setDeletedBy(currentUser);
        log.debug("User audit: User {} deleted by: {}", entity.getId(), currentUser);
    }

    private String getCurrentUsername() {
        try {
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                String username = requestAttributes.getRequest().getHeader("X-Username");
                if (username != null && !username.isEmpty()) {
                    return username;
                }
            }
        } catch (Exception e) {
            log.warn("Error retrieving username from X-Username header", e);
        }
        return "SYSTEM";
    }
}

