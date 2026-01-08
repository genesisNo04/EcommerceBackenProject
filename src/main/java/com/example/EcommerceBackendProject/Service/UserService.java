package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;

import java.util.Optional;

public interface UserService {

    User findById(Long userId);

    User findByUsername(String username);

    User findByEmail(String email);

    User createUser(UserRequestDTO userRequestDTO);

    User updateUser(Long userId, UserRequestDTO userRequestDTO);

    void deleteUser(Long userId);

    User changePassword(Long userId, String newPassword);


}
