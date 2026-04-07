package com.balu.hospital.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorRequestDTO {

    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide the valid email address.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Password must be at least 6 characters")
    private String phone;

    @NotBlank(message = "Specialization is required.")
    private String specialization;

    @NotNull(message = "Experience Years is required.")
    private Integer experienceYears;

    @NotNull(message = "Consultation Fee is required.")
    private BigDecimal consultationFee;

    @NotNull(message = "Availability is required.")
    private boolean isAvailable;
}
