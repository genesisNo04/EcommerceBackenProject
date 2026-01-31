package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserResponseDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Service.UserService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final PageableSortValidator pageableSortValidator;

    public AdminUserController(UserService userService, PageableSortValidator pageableSortValidator) {
        this.userService = userService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createAdmin(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = userService.createAdmin(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDTO(user));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(@RequestParam(required = false) Long userId,
                                                            @RequestParam(required = false) String email,
                                                            @RequestParam(required = false) String username,
                                                            @PageableDefault(size = 10, sort = "username") Pageable pageable) {
        int criteriaCount = 0;
        if (userId != null) criteriaCount++;
        if (email != null) criteriaCount++;
        if (username != null) criteriaCount++;

        if (criteriaCount > 1) {
            throw new IllegalArgumentException("Only one search parameter is allowed");
        }

        pageable = pageableSortValidator.validate(pageable, SortableFields.USER.getFields());
        Page<User> users;

        if (userId != null) {
            users = userService.findById(userId, pageable);
        } else if (email != null) {
            users = userService.findByEmail(email, pageable);
        } else if (username != null) {
            users = userService.findByUsername(username, pageable);
        } else {
            users = userService.findAll(pageable);
        }

        return ResponseEntity.ok(users.map(UserMapper::toDTO));
    }
}
