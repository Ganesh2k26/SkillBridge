package com.ganesh.skillbridge.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AiFeedbackResponse {
    private Long id;
    private Long questionId;
    private String questionTitle;
    private String userAnswer;
    private String feedback;
    private String improvedAnswer;
    private String missingPoints;
    private Integer score;
    private LocalDateTime createdAt;
}
