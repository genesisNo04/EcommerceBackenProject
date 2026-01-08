package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ChangePasswordDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserResponseDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = userService.createUser(userRequestDTO);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = userService.updateUser(id, userRequestDTO);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        return ResponseEntity.ok(userResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
       userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO.getPassword());
        return ResponseEntity.noContent().build();
    }
}
