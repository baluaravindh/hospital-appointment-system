package com.balu.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionResponseDTO {

    private Long id;
    private String patientName;
    private String doctorName;
    private LocalDate appointmentDate;
    private String diagnosis;
    private String medicines;
    private String instructions;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;
}
