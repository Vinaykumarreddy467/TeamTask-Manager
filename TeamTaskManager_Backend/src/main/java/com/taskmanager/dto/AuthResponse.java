package com.taskmanager.dto;

public class AuthResponse {
    private String token;
    private String message;
    private Long userId;
    private String name;
    private String email;

    // Constructors
    public AuthResponse() {
    }

    public AuthResponse(String token, String message, Long userId, String name, String email) {
        this.token = token;
        this.message = message;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
