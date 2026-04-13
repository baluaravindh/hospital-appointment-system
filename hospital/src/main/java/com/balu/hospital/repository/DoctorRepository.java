package com.balu.hospital.repository;

import com.balu.hospital.entity.Appointment;
import com.balu.hospital.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecialization(String specialization);

    List<Doctor> findByAvailable(boolean available);

    List<Doctor> findByFullNameContainingIgnoreCase(String keyword);

}
