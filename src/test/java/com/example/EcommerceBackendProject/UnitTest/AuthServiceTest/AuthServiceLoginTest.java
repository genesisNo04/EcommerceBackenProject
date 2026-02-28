package com.example.EcommerceBackendProject.UnitTest.AuthServiceTest;

import com.example.EcommerceBackendProject.DTO.LoginRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.Entity.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceLoginTest extends BaseAuthServiceTest{

    @Test
    void login_success() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("TestUser", "password");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("mocked-jwt-token");

        LoginResponseDTO responseDTO = authService.login(requestDTO);

        assertEquals("mocked-jwt-token", responseDTO.getAccessToken());
        assertEquals("Bearer", responseDTO.getTokenType());

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void login_authenticationFails() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("TestUser", "password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad Credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(requestDTO));

        verify(authenticationManager).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }
}
