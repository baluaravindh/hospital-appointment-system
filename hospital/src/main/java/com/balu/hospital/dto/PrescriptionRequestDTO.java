package com.balu.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PrescriptionRequestDTO {

    @NotNull(message = "Appointment Id is required.")
    private Long appointmentId;

    @NotBlank(message = "Diagnosis is required.")
    private String diagnosis;

    @NotBlank(message = "Medicines is required.")
    private String medicines;

    @NotBlank(message = "Instructions is required.")
    private String instructions;

    private LocalDate followUpDate;
}
