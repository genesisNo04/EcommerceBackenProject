package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.UserController;

import com.example.EcommerceBackendProject.Controller.UserController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.UserTestFactory;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;




@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private static final String USER_URL = "/v1/users";
    private static final MediaType mediaType = MediaType.APPLICATION_JSON;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void updateUser_success() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "testuserupdate",
                        "userupdate",
                        "lastupdate",
                        "user1update@gmail.com",
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );
        ArgumentCaptor<UserUpdateRequestDTO> captor = ArgumentCaptor.forClass(UserUpdateRequestDTO.class);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userService.updateUser(1L, userUpdateRequestDTO)).thenReturn(updatedUser);

        mockMvc.perform(put(USER_URL)
                .contentType(mediaType)
                .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuserupdate"))
                .andExpect(jsonPath("$.email").value("user1update@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("userupdate"))
                .andExpect(jsonPath("$.lastName").value("lastupdate"))
                .andExpect(jsonPath("$.address").isArray())
                .andExpect(jsonPath("$.phoneNumber").value("+1234567891"));

        UserUpdateRequestDTO captured = captor.capture();
        assertEquals("testuserupdate", captured.getUsername());
        assertEquals("userupdate", captured.getFirstName());
        assertEquals("lastupdate", captured.getLastName());
        assertEquals("user1update@gmail.com", captured.getEmail());
        assertEquals("+1234567891", captured.getPhoneNumber());
        assertEquals(2, captured.getAddress().size());
    }
}
