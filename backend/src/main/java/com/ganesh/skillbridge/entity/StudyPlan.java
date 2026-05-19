package com.ganesh.skillbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "study_plans")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudyPlan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "plan_days")
    private Integer planDays;

    @Column(name = "plan_content", columnDefinition = "LONGTEXT")
    private String planContent;

    @Column(name = "weak_topics", columnDefinition = "TEXT")
    private String weakTopics;

    @Column(name = "readiness_score")
    private Integer readinessScore;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
