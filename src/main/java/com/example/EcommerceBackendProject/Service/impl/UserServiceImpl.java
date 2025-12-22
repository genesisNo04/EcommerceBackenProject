package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.UserService;
import jakarta.transaction.Transactional;
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
    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        oldUser.setFirstName(updatedUser.getFirstName());
        oldUser.setLastName(updatedUser.getLastName());
        oldUser.setAddresses(updatedUser.getAddresses());
        oldUser.setPhoneNumber(updatedUser.getPhoneNumber());
        return userRepository.save(oldUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
