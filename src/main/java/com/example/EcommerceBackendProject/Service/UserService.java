package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;

import java.util.Optional;

public interface UserService {

    User findById(Long userId);

    User findByUsername(String username);

    User findByEmail(String email);

    User createUser(UserRequestDTO userRequestDTO);

    User updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);

    User patchUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);

    void deleteUser(Long userId);

    void changePassword(Long userId, String newPassword);


}
