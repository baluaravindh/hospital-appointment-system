package com.balu.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequestDTO {

    @NotNull(message = "Patient id is required.")
    private Long patientId;

    @NotNull(message = "Doctor id is required.")
    private Long doctorId;

    @NotNull(message = "Appointment date is required.")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required.")
    private LocalTime appointmentTime;

    @NotBlank(message = "Symptoms is required.")
    private String symptoms;
}
