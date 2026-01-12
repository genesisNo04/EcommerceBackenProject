package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressResponseDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@PathVariable Long userId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        Address address = addressService.createAddress(addressRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDTO);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long userId, @PathVariable Long addressId, @Valid @RequestBody AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Address address = addressService.updateAddress(addressId, addressUpdateRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> partiallyUpdateAddress(@PathVariable Long userId, @PathVariable Long addressId, @Valid @RequestBody AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Address address = addressService.updateAddress(addressId, addressUpdateRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress(@PathVariable Long userId) {
        Address address = addressService.getDefaultAddress(userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(@PathVariable Long userId) {
        List<Address> address = addressService.getUserAddresses(userId);
        List<AddressResponseDTO> response = address.stream().map(AddressMapper::toDTO).toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        addressService.setDefaultAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}
