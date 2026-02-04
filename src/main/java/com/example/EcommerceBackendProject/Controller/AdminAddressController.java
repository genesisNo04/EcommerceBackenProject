package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressResponseDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.DTO.PageResponse;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
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
    private final PageableSortValidator pageableSortValidator;

    public AdminAddressController(AddressService addressService, PageableSortValidator pageableSortValidator) {
        this.addressService = addressService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @GetMapping("/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress(@RequestParam Long userId) {
        Address address = addressService.getDefaultAddress(userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @GetMapping
    public ResponseEntity<PageResponse<AddressResponseDTO>> getAllAddresses(@RequestParam(required = false) Long userId,
                                                                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ADDRESS.getFields());
        Page<Address> addresses;

        if (userId != null) {
            addresses = addressService.getUserAddresses(userId, pageable);
        } else {
            addresses = addressService.findAllAddress(pageable);
        }

        Page<AddressResponseDTO> page = addresses.map(AddressMapper::toDTO);
        PageResponse<AddressResponseDTO> response =
                new PageResponse<>(
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isLast());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestParam Long userId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        Address address = addressService.createAddress(addressRequestDTO, userId);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDTO);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        Address address = addressService.updateAnyAddress(addressId, addressRequestDTO);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> partiallyUpdateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Address address = addressService.patchAnyAddress(addressId, addressUpdateRequestDTO);
        AddressResponseDTO addressResponseDTO = AddressMapper.toDTO(address);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAnyAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long addressId) {
        addressService.setDefaultAnyAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
