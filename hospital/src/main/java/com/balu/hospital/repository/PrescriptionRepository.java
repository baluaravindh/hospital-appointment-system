package com.balu.hospital.repository;

import com.balu.hospital.entity.Prescription;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {


    Optional<Prescription> findByAppointmentId(Long appointmentId);
}
