package com.shikshasetu.backend.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String role; // e.g. "STUDENT", "INSTRUCTOR"
}
