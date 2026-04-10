package com.balu.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String token; // ← JWT token
    private String tokenType; // ← always "Bearer"
    private String refreshToken;
}
