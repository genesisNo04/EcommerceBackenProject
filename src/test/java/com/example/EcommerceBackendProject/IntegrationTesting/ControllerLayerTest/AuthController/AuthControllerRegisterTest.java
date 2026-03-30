package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AuthController;

import com.example.EcommerceBackendProject.Controller.AuthController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
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
    AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "City", "State", "Country", "12345", true);
    MediaType mediaType = MediaType.APPLICATION_JSON;
    private static final String REGISTER_URL = "/v1/auth/register";

    @Test
    void registerUser_success() throws Exception {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("token", "bearer");
        when(authService.register(any(UserRequestDTO.class))).thenReturn(loginResponseDTO);

        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", "last", List.of(addressRequestDTO), "+1234567890");
        ArgumentCaptor<UserRequestDTO> captor = ArgumentCaptor.forClass(UserRequestDTO.class);

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("bearer"));

        verify(authService).register(captor.capture());
        UserRequestDTO captured = captor.getValue();
        assertEquals("user1", captured.getUsername());
        assertEquals("test123", captured.getPassword());
        assertEquals("test", captured.getFirstName());
        assertEquals("last", captured.getLastName());
        assertEquals("+1234567890", captured.getPhoneNumber());
        assertEquals("123 Main st", captured.getAddress().getFirst().getStreet());
        assertEquals("City", captured.getAddress().getFirst().getCity());
        assertEquals("State", captured.getAddress().getFirst().getState());
        assertEquals("Country", captured.getAddress().getFirst().getCountry());
        assertEquals("12345", captured.getAddress().getFirst().getZipCode());
        assertTrue(captured.getAddress().getFirst().getIsDefault());
    }

    @Test
    void registerUser_failed_invalidAddress() throws Exception {
        AddressRequestDTO addressRequestInvalidDTO = AddressTestFactory.createAddress("123 Main st", "City", "State", "Country", "12345", null);
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("token", "bearer");
        when(authService.register(any(UserRequestDTO.class))).thenReturn(loginResponseDTO);

        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", "last", List.of(addressRequestInvalidDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoMoreInteractions(authService);
    }

    @Test
    void registerUser_failed_userAlreadyExist() throws Exception {
        when(authService.register(any(UserRequestDTO.class))).thenThrow(new ResourceAlreadyExistsException("username or email is already exists."));

        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", "last", List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("username or email is already exists."));
    }

    @Test
    void registerUser_failed_nullUsername() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO(null, "test123", "test1@gmail.com", "test", "last", List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullPassword() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", null, "test1@gmail.com", "test", "last", List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullEmail() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", null, "test", "last", List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_emailMalformed() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1gmail.com", "test", "last", List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullFirstName() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", null, "last", List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullLastName() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", null, List.of(addressRequestDTO), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullAddress() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", "last", null, "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_emptyAddress() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", "last", List.of(), "+1234567890");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullPhoneNumber() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("user1", "test123", "test1@gmail.com", "test", "last", List.of(addressRequestDTO), null);

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_allCriteriaInvalid() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO(null, null, "test1gmail.com", null, null, List.of(), null);

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_nullBody() throws Exception {

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(mediaType)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void registerUser_failed_notEnforcement() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO(null, null, "test1gmail.com", null, null, List.of(), null);

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }
}
