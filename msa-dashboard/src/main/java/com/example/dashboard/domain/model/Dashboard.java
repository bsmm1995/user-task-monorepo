package com.example.dashboard.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Dashboard {
    private DashboardUser user;
    private DashboardStats statistics;
    private List<DashboardTask> recentTasks;
}
