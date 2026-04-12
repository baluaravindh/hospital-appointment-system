package com.balu.hospital.controller;

import com.balu.hospital.dto.AddressRequestDTO;
import com.balu.hospital.dto.AddressResponseDTO;
import com.balu.hospital.entity.Addresses;
import com.balu.hospital.service.AddressesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressesService addressesService;

    // POST /api/users/1/addresses
    @PostMapping
    public ResponseEntity<AddressResponseDTO> addAddress(
            @PathVariable Long patientId,
            @Valid @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressesService.addAddress(patientId, dto));
    }

    // GET /api/users/1/addresses
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses(@PathVariable Long patientId) {
        return ResponseEntity.ok(addressesService.getAllPatientsAddresses(patientId));
    }

    // DELETE /api/users/1/addresses/2
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Long patientId, @PathVariable Long addressId) {
        addressesService.deleteAddress(addressId);
        return ResponseEntity.ok("Address has been deleted successfully");
    }
}
