package com.ganesh.skillbridge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    @NotBlank private String category;
    @NotBlank private String topic;
    @NotBlank private String difficulty;
    private String questionType;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    @NotBlank private String correctAnswer;
    private String explanation;
    private Integer points;
    private Long companyId;
}
