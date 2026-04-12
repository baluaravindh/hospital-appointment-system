package com.balu.hospital.service;

import com.balu.hospital.dto.AddressRequestDTO;
import com.balu.hospital.dto.AddressResponseDTO;
import com.balu.hospital.entity.Addresses;
import com.balu.hospital.entity.Patient;
import com.balu.hospital.exception.ResourceNotFoundException;
import com.balu.hospital.repository.AddressRepository;
import com.balu.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressesService {

    private final AddressRepository addressRepository;
    private final PatientRepository patientRepository;

    // ADD address to a user
    public AddressResponseDTO addAddress(Long patientId, AddressRequestDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        Addresses address = new Addresses();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());
        address.setAddressType(dto.getAddressType());
        address.setDefault(dto.isDefault());
        address.setPatient(patient);

        Addresses saved = addressRepository.save(address);
        return mapToDto(saved);
    }

    // GET all addresses of a user
    public List<AddressResponseDTO> getAllPatientsAddresses(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return addressRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // DELETE an address
    public void deleteAddress(Long id) {
        Addresses addresses = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
        addressRepository.deleteById(id);
    }

    //---MAPPER---
    private AddressResponseDTO mapToDto(Addresses addresses) {
        return new AddressResponseDTO(
                addresses.getId(),
                addresses.getStreet(),
                addresses.getCity(),
                addresses.getState(),
                addresses.getPincode(),
                addresses.getAddressType(),
                addresses.isDefault(),
                addresses.getPatient().getId()
        );
    }
}
