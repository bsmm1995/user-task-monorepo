package com.example.dashboard.infrastructure.adapter.in.rest;

import com.example.dashboard.application.port.in.GetDashboardUseCase;
import com.example.dashboard.domain.model.Dashboard;
import com.example.dashboard.infrastructure.adapter.in.rest.api.DashboardApiDelegate;
import com.example.dashboard.infrastructure.adapter.in.rest.dto.GetDashboardResponse;
import com.example.dashboard.infrastructure.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardDelegateImpl implements DashboardApiDelegate {

    private final GetDashboardUseCase dashboardUseCase;
    private final DashboardMapper dashboardMapper;

    @Override
    public ResponseEntity<GetDashboardResponse> getUserDashboard(Long userId) {
        // 1. Call the application service (use case)
        Dashboard dashboard = dashboardUseCase.getDashboard(userId);

        // 2. Map domain to DTO
        GetDashboardResponse response = new GetDashboardResponse();
        response.setData(dashboardMapper.toDto(dashboard));

        return ResponseEntity.ok(response);
    }
}
