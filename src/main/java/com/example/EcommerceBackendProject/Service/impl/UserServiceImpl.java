package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Role;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Repository.ShoppingCartRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

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
    @Transactional
    public User createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail()) || userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("username or email is already exists.");
        }

        User user = UserMapper.toEntity(userRequestDTO);
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        user.setRoles(roles);

        ShoppingCart shoppingCart = new ShoppingCart();
        user.setCart(shoppingCart);
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserRequestDTO userRequestDTO) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        oldUser.setFirstName(userRequestDTO.getFirstName());
        oldUser.setLastName(userRequestDTO.getLastName());
//        oldUser.setAddresses(userRequestDTO.getAddress());
        oldUser.setPhoneNumber(userRequestDTO.getPhoneNumber());
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
