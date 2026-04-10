package com.balu.hospital.controller;

import com.balu.hospital.dto.LoginRequestDTO;
import com.balu.hospital.dto.PatientRequestDTO;
import com.balu.hospital.dto.PatientResponseDTO;
import com.balu.hospital.entity.Patient;
import com.balu.hospital.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

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
    public ResponseEntity<PatientResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        PatientResponseDTO response = patientService.login(dto);
        return ResponseEntity.ok(response);
    }

    //GET /api/patients
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }
}
