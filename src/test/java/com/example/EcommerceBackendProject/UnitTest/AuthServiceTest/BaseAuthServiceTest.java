package com.example.EcommerceBackendProject.UnitTest.AuthServiceTest;

import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Service.UserService;
import com.example.EcommerceBackendProject.Service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
abstract class BaseAuthServiceTest {

    @Mock
    public JwtService jwtService;

    @Mock
    public AuthenticationManager authenticationManager;

    @Mock
    public UserService userService;

    @Mock
    public Authentication authentication;

    @InjectMocks
    public AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(jwtService, authenticationManager, userService);
    }
}
