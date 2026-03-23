package com.example.dashboard.application.port.out;

import com.example.dashboard.domain.model.DashboardUser;
import java.util.Optional;

public interface LoadUserPort {
    Optional<DashboardUser> loadUser(Long userId);
}
