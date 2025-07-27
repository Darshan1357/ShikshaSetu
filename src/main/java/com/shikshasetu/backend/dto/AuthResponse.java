package com.shikshasetu.backend.dto;

import com.shikshasetu.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.*;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;
}
