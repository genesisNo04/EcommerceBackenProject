package com.example.EcommerceBackendProject.UnitTest.UserServiceTest;

import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Service.impl.UserServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
abstract class BaseUserServiceTest {

    @Mock
    public PasswordEncoder passwordEncoder;

    @Mock
    public AddressService addressService;

    @InjectMocks
    public UserServiceImpl userService;

    @Mock
    public UserRepository userRepository;

}
