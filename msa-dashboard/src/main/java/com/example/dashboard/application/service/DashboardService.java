package com.example.dashboard.application.service;

import com.example.dashboard.application.port.in.GetDashboardUseCase;
import com.example.dashboard.application.port.out.LoadTasksPort;
import com.example.dashboard.application.port.out.LoadUserPort;
import com.example.dashboard.domain.model.Dashboard;
import com.example.dashboard.domain.model.DashboardStats;
import com.example.dashboard.domain.model.DashboardTask;
import com.example.dashboard.domain.model.DashboardUser;
import com.example.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService implements GetDashboardUseCase {

    private final LoadUserPort userPort;
    private final LoadTasksPort tasksPort;

    @Override
    public Dashboard getDashboard(Long userId) {
        // 1. Load User
        DashboardUser user = userPort.loadUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 2. Load Tasks
        List<DashboardTask> tasks = tasksPort.loadTasks(userId);

        // 3. Calculate Stats
        DashboardStats stats = calculateStats(tasks);

        // 4. Build Dashboard
        return Dashboard.builder()
                .user(user)
                .recentTasks(tasks.stream().limit(5).toList())
                .statistics(stats)
                .build();
    }

    private DashboardStats calculateStats(List<DashboardTask> tasks) {
        int total = tasks.size();
        int pending = (int) tasks.stream().filter(t -> "PENDING".equalsIgnoreCase(t.getStatus())).count();
        int completed = (int) tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
        
        double rate = 0.0;
        if (total > 0) {
             rate = ((double) completed / total) * 100.0;
             // Round to 2 decimal places for cleaner output
             rate = Math.round(rate * 100.0) / 100.0;
        }

        return DashboardStats.builder()
                .totalTasks(total)
                .pendingTasks(pending)
                .completedTasks(completed)
                .completionRate(rate)
                .build();
    }
}
