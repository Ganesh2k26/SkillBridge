package com.ganesh.skillbridge.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String topic;
    private String difficulty;
    private String questionType;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer points;
    private Long companyId;
    private String companyName;
    private String status;
}
