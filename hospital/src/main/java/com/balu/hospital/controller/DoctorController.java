package com.balu.hospital.controller;

import com.balu.hospital.dto.DoctorDto;
import com.balu.hospital.dto.DoctorRequestDTO;
import com.balu.hospital.repository.DoctorRepository;
import com.balu.hospital.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // POST /api/doctors — add doctor
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> addDoctor(@Valid @RequestBody DoctorRequestDTO dto) {
        DoctorDto doctor = doctorService.addDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    // GET /api/doctors — get all doctors
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // GET /api/doctors/{id} — get by id
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DoctorDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // PUT /api/doctors/{id} — update doctor
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDTO dto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
    }

    // DELETE /api/doctors/{id} — delete doctor
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok("Doctor has been deleted successfully");
    }

    // GET /api/doctors/specialization/{specialization} — filter by specialization
    @GetMapping("/specialization/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DoctorDto>> filterBySpecialization(@RequestParam String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }

    // GET /api/doctors/available — get all available doctors
    @GetMapping("/availability/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DoctorDto>> getAllAvailableDoctors(@RequestParam boolean available) {
        return ResponseEntity.ok(doctorService.getAvailableDoctors(available));
    }

    // GET /api/doctors/search?keyword=xyz — search by name
    @GetMapping("/name/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DoctorDto>> searchDoctorsByName(@RequestParam String keyword) {
        return ResponseEntity.ok(doctorService.searchDoctorsByName(keyword));
    }
}
