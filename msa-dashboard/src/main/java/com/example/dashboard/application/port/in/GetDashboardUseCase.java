package com.example.dashboard.application.port.in;

import com.example.dashboard.domain.model.Dashboard;

public interface GetDashboardUseCase {
    Dashboard getDashboard(Long userId);
}
