package com.balu.hospital.service;

import com.balu.hospital.dto.PrescriptionRequestDTO;
import com.balu.hospital.dto.PrescriptionResponseDTO;
import com.balu.hospital.entity.Appointment;
import com.balu.hospital.entity.Prescription;
import com.balu.hospital.exception.ResourceNotFoundException;
import com.balu.hospital.repository.AppointmentRepository;
import com.balu.hospital.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    // ADD PRESCRIPTION
    @Transactional
    public PrescriptionResponseDTO addPrescription(PrescriptionRequestDTO dto) {

        // Step 1: Find the appointment
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + dto.getAppointmentId()));

        // Step 2: Prescription only allowed for COMPLETED appointments
        if (appointment.getStatus() != Appointment.Status.COMPLETED) {
            throw new RuntimeException("Prescription can only be added to COMPLETED appointments. " +
                    "Current status: " + appointment.getStatus());
        }

        // Step 3: Check if prescription already exists for this appointment
        // OneToOne — only one prescription per appointment
        boolean alreadyExists = prescriptionRepository.findByAppointmentId(
                dto.getAppointmentId()
        ).isPresent();

        if (alreadyExists) {
            throw new RuntimeException(
                    "Prescription already exists for appointment id: " + dto.getAppointmentId());
        }

        // Step 4: Create prescription
        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setDiagnosis(dto.getDiagnosis());
        prescription.setMedicines(dto.getMedicines());
        prescription.setInstructions(dto.getInstructions());
        prescription.setFollowUpDate(dto.getFollowUpDate());

        Prescription saved = prescriptionRepository.save(prescription);
        return mapToDto(saved);
    }

    // GET PRESCRIPTION BY APPOINTMENT ID
    public PrescriptionResponseDTO getPrescriptionByAppointmentId(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for appointment id: "
                        + appointmentId));
        return mapToDto(prescription);
    }

    // GET PRESCRIPTION BY ID
    public PrescriptionResponseDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));
        return mapToDto(prescription);
    }

    // GET ALL PRESCRIPTIONS
    public List<PrescriptionResponseDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private PrescriptionResponseDTO mapToDto(Prescription prescription) {
        return new PrescriptionResponseDTO(
                prescription.getId(),
                prescription.getAppointment().getPatient().getFullName(),
                prescription.getAppointment().getDoctor().getFullName(),
                prescription.getAppointment().getAppointmentDate(),
                prescription.getDiagnosis(),
                prescription.getMedicines(),
                prescription.getInstructions(),
                prescription.getFollowUpDate(),
                prescription.getCreatedAt()
        );
    }
}
