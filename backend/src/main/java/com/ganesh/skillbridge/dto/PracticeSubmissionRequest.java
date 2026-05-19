package com.ganesh.skillbridge.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PracticeSubmissionRequest {
    @NotNull private Long questionId;
    @NotBlank private String userAnswer;
    private Integer timeTakenSeconds;
}
