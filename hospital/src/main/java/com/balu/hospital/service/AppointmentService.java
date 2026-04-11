package com.balu.hospital.service;

import com.balu.hospital.dto.AppointmentRequestDTO;
import com.balu.hospital.dto.AppointmentResponseDTO;
import com.balu.hospital.entity.Appointment;
import com.balu.hospital.entity.Doctor;
import com.balu.hospital.entity.Patient;
import com.balu.hospital.exception.ResourceNotFoundException;
import com.balu.hospital.repository.AppointmentRepository;
import com.balu.hospital.repository.DoctorRepository;
import com.balu.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // BOOK APPOINTMENT
    @Transactional
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO dto) {

        // Step 1: Validate patient exists
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + dto.getPatientId()));

        // Step 2: Validate doctor exists
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + dto.getDoctorId()));

        // Step 3: Check if doctor is available
        if (!doctor.isAvailable()) {
            throw new RuntimeException("Doctor " + doctor.getFullName()
                    + " is currently not available for appointments.");
        }

        // Step 4: Check duplicate booking
        // Same patient cannot book same doctor on same date
        boolean duplicateBooking = appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDate(
                dto.getPatientId(),
                dto.getDoctorId(),
                dto.getAppointmentDate()
        );
        if (duplicateBooking) {
            throw new RuntimeException("You already have an appointment with Dr. " +
                    doctor.getFullName() + " on " + dto.getAppointmentDate());
        }

        // Step 5: Check time slot conflict
        // Doctor cannot have two appointments at same date and time
        boolean timeSlotConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                dto.getDoctorId(),
                dto.getAppointmentDate(),
                dto.getAppointmentTime()
        );
        if (timeSlotConflict) {
            throw new RuntimeException("Dr. " + doctor.getFullName() +
                    " already has an appointment at " + dto.getAppointmentTime() + " on "
                    + dto.getAppointmentDate() + ". Please choose a different time slot.");
        }

        // Step 6: All checks passed — create appointment
        // status = SCHEDULED and createdAt auto-set by @PrePersist
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setSymptoms(dto.getSymptoms());

        Appointment saved = appointmentRepository.save(appointment);
        return mapToDto(saved);
    }

    // GET BY ID
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return mapToDto(appointment);
    }

    // GET ALL APPOINTMENTS OF A PATIENT
    public List<AppointmentResponseDTO> getAllAppointmentsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // GET ALL APPOINTMENTS OF A DOCTOR
    public List<AppointmentResponseDTO> getAllAppointmentsByDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // CANCEL APPOINTMENT
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        // Can only cancel SCHEDULED appointments
        if (appointment.getStatus() == Appointment.Status.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed appointment");
        }

        if (appointment.getStatus() == Appointment.Status.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        appointment.setStatus(Appointment.Status.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);

        return mapToDto(updated);
    }

    // COMPLETE APPOINTMENT (Admin only)
    @Transactional
    public AppointmentResponseDTO completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if (appointment.getStatus() == Appointment.Status.CANCELLED) {
            throw new RuntimeException("Cannot complete a cancelled appointment");
        }

        if (appointment.getStatus() == Appointment.Status.COMPLETED) {
            throw new RuntimeException("Appointment is already completed");
        }

        appointment.setStatus(Appointment.Status.COMPLETED);
        Appointment updated = appointmentRepository.save(appointment);

        return mapToDto(updated);
    }

    //---MAPPER---
    private AppointmentResponseDTO mapToDto(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getFullName(),
                appointment.getDoctor().getSpecialization(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus().name(),
                appointment.getSymptoms(),
                appointment.getNotes()
        );
    }
}
