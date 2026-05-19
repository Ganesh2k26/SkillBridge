package com.ganesh.skillbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "topic_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","topic","category"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TopicProgress {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String category;

    @Column(name = "total_questions") @Builder.Default private Integer totalQuestions = 0;
    @Column(name = "attempted")       @Builder.Default private Integer attempted = 0;
    @Column(name = "correct")         @Builder.Default private Integer correct = 0;

    @Column(name = "strength_level")
    @Builder.Default
    private String strengthLevel = "WEAK";   // WEAK | MEDIUM | STRONG

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
