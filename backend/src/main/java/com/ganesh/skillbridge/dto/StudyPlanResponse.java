package com.ganesh.skillbridge.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudyPlanResponse {
    private Long id;
    private String companyName;
    private Integer planDays;
    private String planContent;
    private Integer readinessScore;
    private LocalDateTime createdAt;
}
