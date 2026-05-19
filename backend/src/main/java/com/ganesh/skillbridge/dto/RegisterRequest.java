package com.ganesh.skillbridge.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String name;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    private String collegeName;
    private Integer graduationYear;
    private String targetCompany;
}
