package com.example.dashboard.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    private int totalTasks;
    private int pendingTasks;
    private int completedTasks;
    private double completionRate;
}
