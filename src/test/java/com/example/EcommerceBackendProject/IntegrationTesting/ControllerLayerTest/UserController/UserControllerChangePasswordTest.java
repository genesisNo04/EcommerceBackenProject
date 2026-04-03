package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.UserController;

import com.example.EcommerceBackendProject.Controller.UserController;
import com.example.EcommerceBackendProject.DTO.ChangePasswordDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerChangePasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private JwtService jwtService;

    private static final String USER_URL = "/v1/users";
    private static final MediaType mediaType = MediaType.APPLICATION_JSON;
    ObjectMapper mapper = new ObjectMapper();

    User createTestUser() {
        User user = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        user.setId(1L);
        user.setAddresses(List.of());

        return user;
    }

    @Test
    void changePassword_success() throws Exception {
        User user = createTestUser();

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("updatePassword");

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(patch(USER_URL + "/password")
                .contentType(mediaType)
                .content(mapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
