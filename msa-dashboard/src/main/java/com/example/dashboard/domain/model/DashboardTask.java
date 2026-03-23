package com.example.dashboard.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class DashboardTask {
    private Long id;
    private String title;
    private String status;
    private OffsetDateTime updatedAt;
}
