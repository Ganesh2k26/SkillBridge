package com.ganesh.skillbridge.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class StudyPlanRequest {
    @NotBlank private String companyName;
    @NotNull @Min(3) @Max(14) private Integer planDays;
    private List<String> weakTopics;
}
