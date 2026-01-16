package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Role;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Repository.ShoppingCartRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final AddressService addressService;

    public UserServiceImpl(UserRepository userRepository, ShoppingCartRepository shoppingCartRepository, AddressService addressService) {
        this.userRepository = userRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.addressService = addressService;
    }

    private void validateAndSetEmail(User oldUser, String newEmail) {
        if (newEmail != null && !oldUser.getEmail().equals(newEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new ResourceAlreadyExistsException("Email is already in used");
            }
        }
        oldUser.setEmail(newEmail);
    }

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
        user.setRoles(new ArrayList<>(List.of(Role.USER)));
        user.setAddresses(addressService.resolveAddresses(userRequestDTO.getAddress(), user));

        ShoppingCart shoppingCart = new ShoppingCart();
        user.setCart(shoppingCart);
        shoppingCart.setUser(user);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        oldUser.setFirstName(userUpdateRequestDTO.getFirstName());
        oldUser.setLastName(userUpdateRequestDTO.getLastName());
        oldUser.getAddresses().clear();
        oldUser.getAddresses().addAll(addressService.resolveAddresses(userUpdateRequestDTO.getAddress(), oldUser));
        oldUser.setPhoneNumber(userUpdateRequestDTO.getPhoneNumber());
        validateAndSetEmail(oldUser, userUpdateRequestDTO.getEmail());
        return userRepository.save(oldUser);
    }

    @Override
    @Transactional
    public User patchUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));

        if (userUpdateRequestDTO.getFirstName() != null) {
            oldUser.setFirstName(userUpdateRequestDTO.getFirstName());
        }

        if (userUpdateRequestDTO.getLastName() != null) {
            oldUser.setLastName(userUpdateRequestDTO.getLastName());
        }

        if (userUpdateRequestDTO.getAddress() != null) {
            oldUser.getAddresses().clear();
            oldUser.getAddresses().addAll(addressService.resolveAddresses(userUpdateRequestDTO.getAddress(), oldUser));
        }

        if (userUpdateRequestDTO.getPhoneNumber() != null) {
            oldUser.setPhoneNumber(userUpdateRequestDTO.getPhoneNumber());
        }

        if (userUpdateRequestDTO.getEmail() != null) {
            validateAndSetEmail(oldUser, userUpdateRequestDTO.getEmail());
        }
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
