package com.ganesh.skillbridge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;       // Aptitude | SQL | Java | DSA | HR

    @Column(nullable = false)
    private String topic;          // e.g. Joins, OOP, Arrays, Percentages

    @Column(nullable = false)
    private String difficulty;     // Easy | Medium | Hard

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "option_a")
    private String optionA;
    @Column(name = "option_b")
    private String optionB;
    @Column(name = "option_c")
    private String optionC;
    @Column(name = "option_d")
    private String optionD;

    @Column(name = "question_type")
    @Builder.Default
    private String questionType = "MCQ";  // MCQ | TEXT | CODE | SQL

    @Column(name = "points")
    @Builder.Default
    private Integer points = 10;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
