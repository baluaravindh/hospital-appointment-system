package com.balu.hospital.service;

import com.balu.hospital.dto.*;
import com.balu.hospital.entity.Patient;
import com.balu.hospital.entity.RefreshToken;
import com.balu.hospital.exception.DuplicateEmailException;
import com.balu.hospital.exception.InvalidCredentialsException;
import com.balu.hospital.exception.ResourceNotFoundException;
import com.balu.hospital.repository.PatientRepository;
import com.balu.hospital.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // REGISTER
    public PatientResponseDTO register(PatientRequestDTO dto) {
        // Check if email already exists
        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email Already Exists with that Email: " + dto.getEmail());
        }
        Patient patient = new Patient();
        patient.setFullName(dto.getFullName());
        patient.setEmail(dto.getEmail());
        patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient.setPhone(dto.getPhone());
        patient.setRole(Patient.Role.PATIENT);
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setBloodGroup(dto.getBloodGroup());

        Patient saved = patientRepository.save(patient);
        return mapToDto(saved);
    }

    // LOGIN
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Patient patient = patientRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Patient Not Found with email: " + dto.getEmail()));

        // Compare raw password with encrypted password
        if (!passwordEncoder.matches(dto.getPassword(), patient.getPassword())) {
            throw new InvalidCredentialsException("Invalid Password");
        }

        // Generate JWT token
        String accessToken = jwtUtil.generateToken(patient.getEmail(), patient.getRole().name());

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(patient.getId());

//        return mapToDto(patient);
        return new LoginResponseDTO(
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getRole().name(),
                patient.getDateOfBirth(),
                patient.getBloodGroup(),
                accessToken,
                "Bearer",
                refreshToken.getToken()
        );
    }

    // GET all patients
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void changePassword(String email, ChangePasswordRequestDTO dto) {

        // Step 1: Find patient by email (extracted from JWT)
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

        // Step 2: Verify current password is correct
        if (!passwordEncoder.matches(dto.getCurrentPassword(), patient.getPassword())) {
            throw new RuntimeException("Current Password is incorrect");
        }

        // Step 3: Check new password and confirm password match
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Step 4: Check new password is different from current
        if (passwordEncoder.matches(dto.getNewPassword(), patient.getPassword())) {
            throw new RuntimeException("New password must be different from current password");
        }

        // Step 5: Encode and save new password
        patient.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        patientRepository.save(patient);

        // Step 6: Invalidate all refresh tokens — force re-login on all devices
        refreshTokenService.deleteAllUserTokens(patient.getId());
    }

    //---MAPPER---
    private PatientResponseDTO mapToDto(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getRole().name(),
                patient.getDateOfBirth(),
                patient.getBloodGroup(),
                patient.getCreatedAt()
        );
    }
}
