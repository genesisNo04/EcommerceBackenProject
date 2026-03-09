package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.AuthService;

import com.example.EcommerceBackendProject.DTO.LoginRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void login_success_byUsername() {
        User user = testDataHelper.createUser("testuser", "test123", "test@gmail.com", "test", "user", "+1234567890", List.of());

        LoginResponseDTO dto = authService.login(new LoginRequestDTO("testuser", "test123"));

        assertEquals("Bearer", dto.getTokenType());
        assertNotNull(dto.getAccessToken());

        String username = jwtService.extractUsername(dto.getAccessToken());

        assertEquals("testuser", username);
    }

    @Test
    void login_success_byEmail() {
        testDataHelper.createUser("testuser", "test123", "test@gmail.com", "test", "user", "+1234567890", List.of());

        LoginResponseDTO dto = authService.login(new LoginRequestDTO("test@gmail.com", "test123"));

        assertEquals("Bearer", dto.getTokenType());
        assertNotNull(dto.getAccessToken());

        String username = jwtService.extractUsername(dto.getAccessToken());

        assertEquals("testuser", username);
    }

    @Test
    void loginFailed_incorrectPassword() {
        testDataHelper.createUser("testuser", "test123", "test@gmail.com", "test", "user", "+1234567890", List.of());

        LoginResponseDTO dto = authService.login(new LoginRequestDTO("test@gmail.com", "test123"));

        assertThrows(AuthenticationException.class, () -> authService.login(new LoginRequestDTO("testuser", "test1234")));
    }

    @Test
    void login_failed() {
        assertThrows(AuthenticationException.class, () -> authService.login(new LoginRequestDTO("testuser", "test123")));
    }

    @Test
    void register_success() {
        LoginResponseDTO dto = authService.register(new UserRequestDTO("testuser", "test123", "test@gmail.com", "test", "user", List.of(), "+1234567890"));

        assertEquals("Bearer", dto.getTokenType());
        assertNotNull(dto.getAccessToken());

        String username = jwtService.extractUsername(dto.getAccessToken());

        assertEquals("testuser", username);
    }

    @Test
    void registerFailed_duplicateUsername() {
        testDataHelper.createUser("testuser", "test123", "test@gmail.com", "test", "user", "+1234567890", List.of());

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(new UserRequestDTO("testuser", "test123", "test1@gmail.com", "test", "user", List.of(), "+1234567890")));

        assertEquals("username or email is already exists.", ex.getMessage());
    }

    @Test
    void registerFailed_duplicateEmail() {
        testDataHelper.createUser("testuser", "test123", "test@gmail.com", "test", "user", "+1234567890", List.of());

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(new UserRequestDTO("testuser1", "test123", "test@gmail.com", "test", "user", List.of(), "+1234567890")));

        assertEquals("username or email is already exists.", ex.getMessage());
    }
}
