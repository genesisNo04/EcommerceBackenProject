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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/addresses")
public class AddressController {

    private final AddressService addressService;
    private final SecurityUtils securityUtils;

    public AddressController(AddressService addressService, SecurityUtils securityUtils) {
        this.addressService = addressService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        Long userId = securityUtils.getCurrentUserId();
        Address address = addressService.createAddress(addressRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDTO);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        Long userId = securityUtils.getCurrentUserId();
        Address address = addressService.updateAddress(addressId, addressRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> partiallyUpdateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Long userId = securityUtils.getCurrentUserId();
        Address address = addressService.patchAddress(addressId, addressUpdateRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        Long userId = securityUtils.getCurrentUserId();
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress() {
        Long userId = securityUtils.getCurrentUserId();
        Address address = addressService.getDefaultAddress(userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<AddressResponseDTO>> getUserAddresses(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = securityUtils.getCurrentUserId();
        Page<Address> address = addressService.getUserAddresses(userId, pageable);
        Page<AddressResponseDTO> response = address.map(AddressMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long addressId) {
        Long userId = securityUtils.getCurrentUserId();
        addressService.setDefaultAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}
