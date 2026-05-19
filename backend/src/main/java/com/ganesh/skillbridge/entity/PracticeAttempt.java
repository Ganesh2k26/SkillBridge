package com.ganesh.skillbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "practice_attempts")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PracticeAttempt {

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

    @Column(name = "is_correct")
    @Builder.Default
    private Boolean isCorrect = false;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ATTEMPTED";   // ATTEMPTED | CORRECT | NEEDS_REVISION

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "attempted_at")
    @Builder.Default
    private LocalDateTime attemptedAt = LocalDateTime.now();
}
