package com.balu.hospital.controller;

import com.balu.hospital.dto.PrescriptionRequestDTO;
import com.balu.hospital.dto.PrescriptionResponseDTO;
import com.balu.hospital.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // POST /api/prescriptions — ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrescriptionResponseDTO> addPrescription(
            @Valid @RequestBody PrescriptionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.addPrescription(dto));
    }

    // GET /api/prescriptions/appointment/{appointmentId}
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrescriptionResponseDTO> getPrescriptionsByAppointmentId(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionByAppointmentId(appointmentId));
    }

    // GET /api/prescriptions/{id}
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrescriptionResponseDTO> getPrescriptionsById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    // GET /api/prescriptions — ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrescriptionResponseDTO>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }
}
