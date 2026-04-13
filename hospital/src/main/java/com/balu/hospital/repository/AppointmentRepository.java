package com.balu.hospital.repository;

import com.balu.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    // Duplicate booking check
    boolean existsByPatientIdAndDoctorIdAndAppointmentDate(
            Long patientId, Long doctorId, LocalDate date
    );

    // Time slot conflict check
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId, LocalDate date, LocalTime time
    );

    boolean existsByDoctorIdAndStatus(Long doctorId, Appointment.Status status);
}
