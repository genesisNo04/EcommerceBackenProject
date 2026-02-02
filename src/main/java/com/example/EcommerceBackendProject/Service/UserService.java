package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    User findById(Long userId);

    User findByUsername(String username);

    User findByEmail(String email);

    Page<User> findById(Long userId, Pageable pageable);

    Page<User> findByUsername(String username, Pageable pageable);

    Page<User> findByEmail(String email, Pageable pageable);

    User createCustomerUser(UserRequestDTO userRequestDTO);

    User updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);

    User patchUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);

    void deleteUser(Long userId);

    void changePassword(Long userId, String newPassword);

    User createAdmin(UserRequestDTO userRequestDTO);

    Page<User> findAll(Pageable pageable);
}
