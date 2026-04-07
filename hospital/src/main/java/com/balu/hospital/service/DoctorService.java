package com.balu.hospital.service;

import com.balu.hospital.dto.DoctorDto;
import com.balu.hospital.dto.DoctorRequestDTO;
import com.balu.hospital.entity.Doctor;
import com.balu.hospital.exception.ResourceNotFoundException;
import com.balu.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    // addDoctor
    public DoctorDto addDoctor(DoctorRequestDTO dto) {
        Doctor doctor = mapToEntity(dto);
        Doctor saved = doctorRepository.save(doctor);
        return mapToDto(saved);
    }

    // getAllDoctors
    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // getDoctorById
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Not Found with id: " + id));
        return mapToDto(doctor);
    }

    // updateDoctor
    public DoctorDto updateDoctor(Long id, DoctorRequestDTO dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Not Found with id: " + id));
        doctor.setFullName(dto.getFullName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setAvailable(dto.isAvailable());

        Doctor saved = doctorRepository.save(doctor);
        return mapToDto(saved);
    }

    // deleteDoctor
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Not Found with id: " + id));
        doctorRepository.delete(doctor);
    }

    // getDoctorsBySpecialization
    public List<DoctorDto> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // getAvailableDoctors
    public List<DoctorDto> getAvailableDoctors(boolean available) {
        return doctorRepository.findByAvailable(available)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // searchDoctorsByName
    public List<DoctorDto> searchDoctorsByName(String keyword) {
        return doctorRepository.findByFullNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    //---MAPPER---
    private DoctorDto mapToDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();
        dto.setId(doctor.getId());
        dto.setFullName(doctor.getFullName());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setAvailable(doctor.isAvailable());
        return dto;
    }

    private Doctor mapToEntity(DoctorRequestDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setFullName(dto.getFullName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setAvailable(doctor.isAvailable());
        return doctor;
    }
}
