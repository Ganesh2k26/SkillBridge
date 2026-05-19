package com.ganesh.skillbridge.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private Long userId;
    private String userName;
    private String targetCompany;
    private Long totalAttempted;
    private Long totalCorrect;
    private Double accuracyPercent;
    private Integer readinessScore;
    private List<TopicStat> weakAreas;
    private List<TopicStat> strongAreas;
    private List<CategoryStat> categoryBreakdown;
    private RecentActivity recentActivity;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopicStat {
        private String topic;
        private String category;
        private Integer attempted;
        private Integer correct;
        private String strengthLevel;
        private Double accuracy;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryStat {
        private String category;
        private long attempted;
        private long correct;
        private double accuracy;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RecentActivity {
        private List<String> lastAttemptedTopics;
        private LocalDateTime lastActiveAt;
        private Integer streakDays;
    }
}
