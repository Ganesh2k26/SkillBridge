package com.ganesh.skillbridge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "companies")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Company {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "test_pattern", columnDefinition = "TEXT")
    private String testPattern;

    @Column(name = "difficulty_level")
    private String difficultyLevel;        // Easy | Medium | Hard

    @Column(name = "avg_package")
    private String avgPackage;

    @Column(name = "question_count")
    @Builder.Default
    private Integer questionCount = 0;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Question> questions;
}
