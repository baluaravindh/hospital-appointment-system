package com.balu.hospital.service;

import com.balu.hospital.entity.Patient;
import com.balu.hospital.entity.RefreshToken;
import com.balu.hospital.exception.ResourceNotFoundException;
import com.balu.hospital.repository.PatientRepository;
import com.balu.hospital.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // CREATE a new refresh token for a user
    @Transactional
    public RefreshToken createRefreshToken(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // Delete any existing refresh token for this user
        refreshTokenRepository.deleteByPatientId(patientId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setPatient(patient);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    // VALIDATE refresh token
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired. Please login again.");
        }
        return refreshToken;
    }

    // REVOKE on logout
    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    // DELETE all tokens for user (force logout all devices)
    @Transactional
    public void deleteAllUserTokens(Long patientId) {
        refreshTokenRepository.deleteByPatientId(patientId);
    }

}
