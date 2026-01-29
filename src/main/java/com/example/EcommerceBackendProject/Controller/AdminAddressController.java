package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.AddressResponseDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            addresses = addressService.findAll(pageable);
        }

        Page<AddressResponseDTO> response = addresses.map(AddressMapper::toDTO);
        return ResponseEntity.ok(response);
    }
}
