package com.balu.hospital.service;

import com.balu.hospital.dto.LoginRequestDTO;
import com.balu.hospital.dto.PatientRequestDTO;
import com.balu.hospital.dto.PatientResponseDTO;
import com.balu.hospital.entity.Patient;
import com.balu.hospital.exception.DuplicateEmailException;
import com.balu.hospital.exception.InvalidCredentialsException;
import com.balu.hospital.exception.ResourceNotFoundException;
import com.balu.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
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
    public PatientResponseDTO login(LoginRequestDTO dto) {
        Patient patient = patientRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Patient Not Found with email: " + dto.getEmail()));

        // Compare raw password with encrypted password
        if (!passwordEncoder.matches(dto.getPassword(), patient.getPassword())) {
            throw new InvalidCredentialsException("Invalid Password");
        }

        return mapToDto(patient);
    }

    // GET all patients
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    //---MAPPER---
    private PatientResponseDTO mapToDto(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDateOfBirth(),
                patient.getBloodGroup(),
                patient.getRole().name(),
                patient.getCreatedAt()
        );
    }
}
