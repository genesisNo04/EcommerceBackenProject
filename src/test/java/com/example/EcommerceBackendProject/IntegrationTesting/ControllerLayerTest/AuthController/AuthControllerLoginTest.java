package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AuthController;

import com.example.EcommerceBackendProject.Controller.AuthController;
import com.example.EcommerceBackendProject.DTO.LoginRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private static final String LOGIN_URL = "/v1/auth/login";
    MediaType mediaType = MediaType.APPLICATION_JSON;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void loginUser_success() throws Exception {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("token", "bearer");
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user1", "test123");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);

        ArgumentCaptor<LoginRequestDTO> captor = ArgumentCaptor.forClass(LoginRequestDTO.class);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("bearer"));

        verify(authService).login(captor.capture());
        LoginRequestDTO captured = captor.getValue();
        assertEquals("user1", captured.getIdentifier());
        assertEquals("test123", captured.getPassword());
    }

    @Test
    void loginUser_failed_emptyBody() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(mediaType)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void loginUser_failed_unSupportMediaType() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user1", "test123");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(mapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void loginUser_failed_incorrectPassword() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user1", "test123");

        when(authService.login(any(LoginRequestDTO.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.accessToken").doesNotExist());
    }

    @Test
    void loginUser_failed_identifierNull() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(null, "test123");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService, jwtService);
    }

    @Test
    void loginUser_failed_identifierBlank() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("", "test123");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService, jwtService);
    }

    @Test
    void loginUser_failed_passwordNull() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user", null);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService, jwtService);
    }

    @Test
    void loginUser_failed_passwordBlank() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user", "");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService, jwtService);
    }
}
