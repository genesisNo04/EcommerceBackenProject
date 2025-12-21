package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()) || userRepository.existsByUsername(user.getUsername())) {
            throw new ResourceAlreadyExistsException("username or email is already exists.");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        return userRepository.save(oldUser);
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public void changePassword(Long userId, String newPassword) {

    }
}
