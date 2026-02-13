package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ChangePasswordDTO;
import com.example.EcommerceBackendProject.DTO.UserResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        Long id = securityUtils.getCurrentUserId();
        User user = userService.updateUser(id, userUpdateRequestDTO);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        return ResponseEntity.ok(userResponseDTO);
    }

    @PatchMapping
    public ResponseEntity<UserResponseDTO> partiallyUpdateUser(@Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        Long id = securityUtils.getCurrentUserId();
        User user = userService.patchUser(id, userUpdateRequestDTO);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        return ResponseEntity.ok(userResponseDTO);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        Long id = securityUtils.getCurrentUserId();
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changeUserPassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        Long id = securityUtils.getCurrentUserId();
        userService.changePassword(id, changePasswordDTO.getPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username")
    public ResponseEntity<UserResponseDTO> getUserByUsername() {
        String username = securityUtils.getCurrentUsername();
        User user = userService.findByUsername(username);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDTO> getUserByEmail() {
        String email = securityUtils.getCurrentEmail();
        User user = userService.findByEmail(email);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        return ResponseEntity.ok(userResponseDTO);
    }
}
