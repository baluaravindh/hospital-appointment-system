package com.balu.hospital.controller;

import com.balu.hospital.dto.*;
import com.balu.hospital.entity.Patient;
import com.balu.hospital.entity.RefreshToken;
import com.balu.hospital.security.JwtUtil;
import com.balu.hospital.service.PatientService;
import com.balu.hospital.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    //POST /api/patient/register
    @PostMapping("/register")
    public ResponseEntity<PatientResponseDTO> register(
            @Valid
            @RequestBody PatientRequestDTO dto) {
        PatientResponseDTO response = patientService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //POST /api/patient/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = patientService.login(dto);
        return ResponseEntity.ok(response);
    }

    // POST /api/users/refresh
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO dto) {

        // Validate refresh token
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(dto.getRefreshToken());

        Patient patient = refreshToken.getPatient();

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(
                patient.getEmail(), patient.getRole().name());

        // Generate new refresh token (rotate for security)
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(patient.getId());

        LoginResponseDTO response = new LoginResponseDTO(
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getRole().name(),
                patient.getDateOfBirth(),
                patient.getBloodGroup(),
                newAccessToken,
                "Bearer",
                newRefreshToken.getToken()
        );
        return ResponseEntity.ok(response);
    }

    //GET /api/patients
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // POST /api/users/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        refreshTokenService.revokeRefreshToken(dto.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    // POST /api/users/change-password
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO dto) {
        // Get logged-in user's email from Security Context
        // This was set by JwtFilter when it validated the token
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // getName() returns the email we set as subject in JWT

        patientService.changePassword(email, dto);

        return ResponseEntity.ok("Password changed successfully. Please login again.");
    }
}
