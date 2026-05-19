package com.ganesh.skillbridge.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PracticeSubmissionResponse {
    private Boolean isCorrect;
    private String correctAnswer;
    private String explanation;
    private String status;
    private Integer pointsEarned;
    private String message;
}
