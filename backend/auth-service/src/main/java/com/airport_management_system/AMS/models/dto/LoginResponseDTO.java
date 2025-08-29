package com.airport_management_system.AMS.models.dto;


public class LoginResponseDTO {
    private String token;
    private String role;
    private Long userId;

    public LoginResponseDTO(String token, String role, Long userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public Long getUserId() { return userId; }
}
