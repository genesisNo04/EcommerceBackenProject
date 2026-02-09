package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final PasswordEncoder passwordEncoder;
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, AddressService addressService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
    }

    private void validateAndSetEmail(User oldUser, String newEmail) {
        if (newEmail != null && !oldUser.getEmail().equals(newEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new ResourceAlreadyExistsException("Email is already in use");
            }
        }
        oldUser.setEmail(newEmail);
        log.info("SET email for user [targetUserId={}]", oldUser.getId());
    }

    private void validateAndSetUsername(User oldUser, String username) {
        if (username != null && !oldUser.getUsername().equals(username)) {
            if (userRepository.existsByUsername(username)) {
                throw new ResourceAlreadyExistsException("Username is already in use");
            }
        }
        oldUser.setUsername(username);
        log.info("SET username for user [targetUserId={}]", oldUser.getId());
    }

    @Override
    public User findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        log.info("FETCHED user [targetUserId={}]", user.getId());
        return user;
    }

    @Override
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        log.info("FETCHED user [targetUserId={}]", user.getId());
        return user;
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        log.info("FETCHED user [targetUserId={}]", user.getId());
        return user;
    }

    @Override
    @Transactional
    public User createCustomerUser(UserRequestDTO userRequestDTO) {
        User user = createUser(userRequestDTO);
        user.assignUserRole();

        ShoppingCart shoppingCart = new ShoppingCart();
        user.setCart(shoppingCart);
        shoppingCart.setUser(user);

        User savedUser = userRepository.save(user);
        log.info("CREATED user [targetUserId={}]", savedUser.getId());
        return savedUser;
    }

    private User createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())
                || userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("username or email is already exists.");
        }

        User user = UserMapper.toEntity(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setAddresses(addressService.resolveAddresses(userRequestDTO.getAddress(), user));

        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));

        validateAndSetUsername(oldUser, userUpdateRequestDTO.getUsername());
        oldUser.setFirstName(userUpdateRequestDTO.getFirstName());
        oldUser.setLastName(userUpdateRequestDTO.getLastName());
        oldUser.getAddresses().clear();
        oldUser.getAddresses().addAll(addressService.resolveAddresses(userUpdateRequestDTO.getAddress(), oldUser));
        oldUser.setPhoneNumber(userUpdateRequestDTO.getPhoneNumber());
        validateAndSetEmail(oldUser, userUpdateRequestDTO.getEmail());
        log.info("UPDATED user [targetUserId={}]", oldUser.getId());
        return oldUser;
    }

    @Override
    @Transactional
    public User patchUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));

        if (userUpdateRequestDTO.getUsername() != null) {
            validateAndSetUsername(oldUser, userUpdateRequestDTO.getUsername());
        }

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

        log.info("PATCHED user [targetUserId={}]", oldUser.getId());
        return oldUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));

        userRepository.delete(user);
        log.info("DELETED user [targetUserId={}]", userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("UPDATED password for user [targetUserId={}]", userId);
    }

    @Override
    @Transactional
    public User createAdmin(UserRequestDTO userRequestDTO) {
        User user = createUser(userRequestDTO);
        user.assignAdminRole();
        User savedUser = userRepository.save(user);
        log.info("CREATED admin user [targetUserId={}]", savedUser.getId());
        return savedUser;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        log.info("FETCH users [total={}]", users.getTotalElements());
        return users;
    }

    @Override
    public Page<User> findById(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        log.info("FETCH users [targetUserId={}]", userId);

        Pageable singleItemPageable =
                PageRequest.of(pageable.getPageNumber(), 1, pageable.getSort());

        return new PageImpl<>(List.of(user), singleItemPageable, 1);
    }

    @Override
    public Page<User> findByUsername(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));

        log.info("FETCH users [targetUserId={}]", user.getId());

        Pageable singleItemPageable =
                PageRequest.of(pageable.getPageNumber(), 1, pageable.getSort());
        return new PageImpl<>(List.of(user), singleItemPageable, 1);
    }

    @Override
    public Page<User> findByEmail(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoResourceFoundException("User not found"));
        log.info("FETCH users [targetUserId={}]", user.getId());

        Pageable singleItemPageable =
                PageRequest.of(pageable.getPageNumber(), 1, pageable.getSort());
        return new PageImpl<>(List.of(user), singleItemPageable, 1);
    }
}
