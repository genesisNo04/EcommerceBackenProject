package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.User;

import java.util.Optional;

public interface UserService {

    User findById(Long userId);

    User findByUsername(String username);

    User findByEmail(String email);

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    void changePassword(Long userId, String newPassword);


}
