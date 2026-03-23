package com.example.dashboard.application.port.out;

import com.example.dashboard.domain.model.DashboardTask;
import java.util.List;

public interface LoadTasksPort {
    List<DashboardTask> loadTasks(Long userId);
}
