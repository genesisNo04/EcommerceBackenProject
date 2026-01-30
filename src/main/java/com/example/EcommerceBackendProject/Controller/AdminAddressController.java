package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressResponseDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/addresses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAddressController {

    private final AddressService addressService;

    public AdminAddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<Page<AddressResponseDTO>> getAllAddresses(@RequestParam(required = false) Long userId,
                                                                    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Address> addresses;

        if (userId != null) {
            addresses = addressService.getUserAddresses(userId, pageable);
        } else {
            addresses = addressService.findAllAddress(pageable);
        }

        Page<AddressResponseDTO> response = addresses.map(AddressMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestParam Long userId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        Address address = addressService.createAddress(addressRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDTO);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@RequestParam Long userId, @PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        Address address = addressService.updateAddress(addressId, addressRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> partiallyUpdateAddress(@RequestParam Long userId, @PathVariable Long addressId, @Valid @RequestBody AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Address address = addressService.patchAddress(addressId, addressUpdateRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@RequestParam Long userId, @PathVariable Long addressId) {
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}
