package com.example.EcommerceBackendProject.UnitTest.AuthServiceTest;

import com.example.EcommerceBackendProject.DTO.LoginRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.CustomUserDetails;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class AuthServiceRegisterTest extends BaseAuthServiceTest{

    @Test
    void register_success() {
        UserRequestDTO userRequestDTO = new UserRequestDTO("testuser", "password", "user@gmail.com", "user", "test", List.of(), "+84123456789");
        User user = UserMapper.toEntity(userRequestDTO);

        when(userService.createCustomerUser(userRequestDTO)).thenReturn(user);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("mocked-jwt-token");

        LoginResponseDTO responseDTO = authService.register(userRequestDTO);

        assertEquals("mocked-jwt-token", responseDTO.getAccessToken());
        assertEquals("Bearer", responseDTO.getTokenType());

        verify(userService).createCustomerUser(userRequestDTO);
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void register_userServiceThrows() {
        UserRequestDTO userRequestDTO = new UserRequestDTO("testuser", "password", "user@gmail.com", "user", "test", List.of(), "+84123456789");

        when(userService.createCustomerUser(userRequestDTO)).thenThrow(new ResourceAlreadyExistsException("username or email is already exists."));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(userRequestDTO));

        assertEquals("username or email is already exists.", ex.getMessage());

        verify(userService).createCustomerUser(userRequestDTO);
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }
}
