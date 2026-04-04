package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.UserController;

import com.example.EcommerceBackendProject.Controller.UserController;
import com.example.EcommerceBackendProject.DTO.ChangePasswordDTO;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private JwtService jwtService;

    private static final String USER_URL = "/v1/users";

    @Test
    void deleteUser_success() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(delete(USER_URL))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(securityUtils).getCurrentUserId();
        verify(userService).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUser_failed_noAuthentication() throws Exception {
        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("No authentication"));

        mockMvc.perform(delete(USER_URL))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message", containsString("No authentication")))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void deleteUser_failed_noUserFound() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        doThrow(new NoResourceFoundException("User not found")).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete(USER_URL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("User not found")))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(userService).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }
}
