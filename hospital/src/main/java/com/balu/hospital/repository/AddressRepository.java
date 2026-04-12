package com.balu.hospital.repository;

import com.balu.hospital.entity.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Addresses, Long> {

    List<Addresses> findByPatientId(Long patientId);
}
