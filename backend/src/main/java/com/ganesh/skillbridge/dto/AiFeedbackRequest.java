package com.ganesh.skillbridge.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AiFeedbackRequest {
    @NotNull private Long questionId;
    @NotBlank private String userAnswer;
}
