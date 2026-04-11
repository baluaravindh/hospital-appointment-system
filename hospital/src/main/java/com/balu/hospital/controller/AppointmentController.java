package com.balu.hospital.controller;

import com.balu.hospital.dto.AppointmentRequestDTO;
import com.balu.hospital.dto.AppointmentResponseDTO;
import com.balu.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // POST /api/appointments — book appointment (PATIENT only)
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            @Valid @RequestBody AppointmentRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.bookAppointment(dto));
    }

    // GET /api/appointments/{id}
    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // GET /api/appointments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointmentsByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsByPatient(patientId));
    }

    // GET /api/appointments/doctor/{doctorId}
    @GetMapping("doctor/{doctorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointmentsByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsByDoctor(doctorId));
    }

    // PATCH /api/appointments/{id}/cancel — PATIENT role
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    // PATCH /api/appointments/{id}/complete — ADMIN role
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }
}
