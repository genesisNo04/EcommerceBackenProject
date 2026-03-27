package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AuthController;

import com.example.EcommerceBackendProject.Controller.AuthController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerRegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void registerUser_success() throws Exception {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("token", "bearer");
        when(authService.register(any(UserRequestDTO.class))).thenReturn(loginResponseDTO);

        AddressRequestDTO addressRequestDTO = new AddressRequestDTO("123 Main st", "City", "State", "Country", "12345", true);
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", "last", List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("bearer"));

        verify(authService).register(any(UserRequestDTO.class));
    }

    @Test
    void registerUser_failed_nullUsername() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "password": "test123",
                                    "email": "test1@gmail.com",
                                    "firstName": "test",
                                    "lastName": "last",
                                    "phoneNumber": "+1234567890",
                                    "address": [
                                        {
                                            "street": "123 Main st",
                                            "city": "City",
                                            "state": "State",
                                            "country": "Country",
                                            "zipCode": "12345",
                                            "isDefault": true
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullPassword() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "email": "test1@gmail.com",
                                    "firstName": "test",
                                    "lastName": "last",
                                    "phoneNumber": "+1234567890",
                                    "address": [
                                        {
                                            "street": "123 Main st",
                                            "city": "City",
                                            "state": "State",
                                            "country": "Country",
                                            "zipCode": "12345",
                                            "isDefault": true
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullEmail() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "test123",
                                    "firstName": "test",
                                    "lastName": "last",
                                    "phoneNumber": "+1234567890",
                                    "address": [
                                        {
                                            "street": "123 Main st",
                                            "city": "City",
                                            "state": "State",
                                            "country": "Country",
                                            "zipCode": "12345",
                                            "isDefault": true
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_emailMalformed() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "test123",
                                    "email": "test1gmail.com",
                                    "firstName": "test",
                                    "lastName": "last",
                                    "phoneNumber": "+1234567890",
                                    "address": [
                                        {
                                            "street": "123 Main st",
                                            "city": "City",
                                            "state": "State",
                                            "country": "Country",
                                            "zipCode": "12345",
                                            "isDefault": true
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullFirstName() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "test123",
                                    "email": "test1@gmail.com",
                                    "lastName": "last",
                                    "phoneNumber": "+1234567890",
                                    "address": [
                                        {
                                            "street": "123 Main st",
                                            "city": "City",
                                            "state": "State",
                                            "country": "Country",
                                            "zipCode": "12345",
                                            "isDefault": true
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullLastName() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "test123",
                                    "email": "test1@gmail.com",
                                    "firstName": "user",
                                    "phoneNumber": "+1234567890",
                                    "address": [
                                        {
                                            "street": "123 Main st",
                                            "city": "City",
                                            "state": "State",
                                            "country": "Country",
                                            "zipCode": "12345",
                                            "isDefault": true
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullAddress() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "test123",
                                    "email": "test1@gmail.com",
                                    "firstName": "user",
                                    "lastName": "last",
                                    "phoneNumber": "+1234567890"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_emptyAddress() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "test123",
                                    "email": "test1@gmail.com",
                                    "firstName": "user",
                                    "lastName": "last",
                                    "phoneNumber": "+1234567890",
                                    "address": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullPhoneNumber() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "test123",
                                    "email": "test1@gmail.com",
                                    "firstName": "test",
                                    "lastName": "last",
                                    "address": [
                                        {
                                            "street": "123 Main st",
                                            "city": "City",
                                            "state": "State",
                                            "country": "Country",
                                            "zipCode": "12345",
                                            "isDefault": true
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }
}
