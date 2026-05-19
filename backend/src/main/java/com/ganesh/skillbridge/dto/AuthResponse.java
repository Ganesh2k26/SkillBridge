package com.ganesh.skillbridge.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;
    private String targetCompany;
    private String message;
}
