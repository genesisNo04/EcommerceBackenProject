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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

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

    @Test
    void changePassword_success() throws Exception {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("updatePassword");

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(patch(USER_URL + "/password")
                .contentType(mediaType)
                .content(mapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(securityUtils).getCurrentUserId();
        verify(userService).changePassword(eq(1L), eq("updatePassword"));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changePassword_failed_blankPassword() throws Exception {
        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(new ChangePasswordDTO(null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("password: must not be blank"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void changePassword_failed_lessThan8Chars() throws Exception {
        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(new ChangePasswordDTO("1234567"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("password: size must be between 8 and 20"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void changePassword_failed_moreThan20Chars() throws Exception {
        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(new ChangePasswordDTO("123456765465498798725"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("password: size must be between 8 and 20"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void changePassword_failed_unsupportedMedia() throws Exception {
        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(mapper.writeValueAsString(new ChangePasswordDTO("123456789"))))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.message").value("Content-Type 'text/plain' is not supported"))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void changePassword_failed_emptyBody() throws Exception {
        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(mediaType)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("Required request body is missing")))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void changePassword_failed_malformedBody() throws Exception {
        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(mediaType)
                        .content("***{jsosn}***"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("JSON parse error")))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void changePassword_failed_noAuthentication() throws Exception {
        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("No authentication"));

        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(new ChangePasswordDTO("updatePassword"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message", containsString("No authentication")))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void changePassword_failed_noUserFound() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        doThrow(new NoResourceFoundException("User not found")).when(userService).changePassword(anyLong(), anyString());

        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(new ChangePasswordDTO("updatePassword"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("User not found")))
                .andExpect(jsonPath("$.path").value(USER_URL + "/password"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
