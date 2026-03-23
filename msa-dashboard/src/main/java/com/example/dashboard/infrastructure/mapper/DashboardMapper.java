package com.example.dashboard.infrastructure.mapper;

import com.example.dashboard.domain.model.Dashboard;
import com.example.dashboard.domain.model.DashboardStats;
import com.example.dashboard.domain.model.DashboardTask;
import com.example.dashboard.domain.model.DashboardUser;
import com.example.dashboard.infrastructure.adapter.in.rest.dto.DashboardResponse;
import com.example.dashboard.infrastructure.adapter.in.rest.dto.TaskStatistics;
import com.example.dashboard.infrastructure.adapter.in.rest.dto.TaskSummary;
import com.example.dashboard.infrastructure.adapter.in.rest.dto.UserSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    @Mapping(source = "completionRate", target = "completionRate")
    TaskStatistics toDto(DashboardStats domain);
    
    UserSummary toDto(DashboardUser domain);
    
    TaskSummary toDto(DashboardTask domain);

    DashboardResponse toDto(Dashboard domain);
}
