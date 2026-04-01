package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.UserController;

import com.example.EcommerceBackendProject.Controller.UserController;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private static final String USER_URL = "/v1/users";
    User user;

    @BeforeEach
    void createUser() {
        user = new User(null, "+1234567890", "last", "user", "test123", "user1@gmail.com", "testuser");
        user.setId(1L);
    }

    @Test
    void getUserByUsername_success() throws Exception {
        when(securityUtils.getCurrentUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get(USER_URL + "/username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("user1@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("user"))
                .andExpect(jsonPath("$.lastName").value("last"))
                .andExpect(jsonPath("$.address").isArray())
                .andExpect(jsonPath("$.phoneNumber").value("+1234567890"));

        verify(securityUtils).getCurrentUsername();
        verify(userService).findByUsername("testuser");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByUsername_failed_nullUsernameFromSecurity() throws Exception {
        when(securityUtils.getCurrentUsername()).thenThrow(new UserAccessDeniedException("No authentication"));

        mockMvc.perform(get(USER_URL + "/username"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/username"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("No authentication"));

        verify(securityUtils).getCurrentUsername();
        verify(userService, never()).findByUsername("testuser");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByUsername_failed_noUserFound() throws Exception {
        when(securityUtils.getCurrentUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenThrow(new NoResourceFoundException("No user found"));

        mockMvc.perform(get(USER_URL + "/username"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/username"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("No user found"));

        verify(securityUtils).getCurrentUsername();
        verify(userService).findByUsername("testuser");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByEmail_success() throws Exception {
        when(securityUtils.getCurrentEmail()).thenReturn("user1@gmail.com");
        when(userService.findByEmail("user1@gmail.com")).thenReturn(user);

        mockMvc.perform(get(USER_URL + "/email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("user1@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("user"))
                .andExpect(jsonPath("$.lastName").value("last"))
                .andExpect(jsonPath("$.address").isArray())
                .andExpect(jsonPath("$.phoneNumber").value("+1234567890"));

        verify(securityUtils).getCurrentEmail();
        verify(userService).findByEmail("user1@gmail.com");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByEmail_failed_noUserFound() throws Exception {
        when(securityUtils.getCurrentEmail()).thenReturn("user1@gmail.com");
        when(userService.findByEmail("user1@gmail.com")).thenThrow(new NoResourceFoundException("No user found"));

        mockMvc.perform(get(USER_URL + "/email"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/email"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("No user found"));

        verify(securityUtils).getCurrentEmail();
        verify(userService).findByEmail("user1@gmail.com");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByEmail_failed_nullEmailFromSecurity() throws Exception {
        when(securityUtils.getCurrentEmail()).thenThrow(new UserAccessDeniedException("No authentication"));

        mockMvc.perform(get(USER_URL + "/email"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/email"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("No authentication"));

        verify(securityUtils).getCurrentEmail();
        verify(userService, never()).findByEmail("user1@gmail.com");
        verifyNoMoreInteractions(userService);
    }
}
