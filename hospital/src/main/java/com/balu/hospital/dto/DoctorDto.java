package com.balu.hospital.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    public boolean isAvailable;
}
