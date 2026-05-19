package com.ganesh.skillbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "ai_feedback")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AiFeedback {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "improved_answer", columnDefinition = "TEXT")
    private String improvedAnswer;

    @Column(name = "missing_points", columnDefinition = "TEXT")
    private String missingPoints;

    @Column(name = "score")
    @Builder.Default
    private Integer score = 0;               // 0-100

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
