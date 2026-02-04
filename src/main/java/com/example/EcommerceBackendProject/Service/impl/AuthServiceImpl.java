package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.LoginRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.CustomUserDetails;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Service.AuthService;
import com.example.EcommerceBackendProject.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getIdentifier(),
                        loginRequestDTO.getPassword()
                )
        );

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(customUserDetails);

        log.info("LOGIN attempt for identifier={}", loginRequestDTO.getIdentifier());

        return new LoginResponseDTO(
                token,
                "Bearer"
        );
    }

    @Override
    public LoginResponseDTO register(UserRequestDTO userRequestDTO) {
        userService.createCustomerUser(userRequestDTO);

        log.info("REGISTER with identifier={}", userRequestDTO.getUsername());

        return login(new LoginRequestDTO(
                userRequestDTO.getUsername(),
                userRequestDTO.getPassword()));
    }
}
