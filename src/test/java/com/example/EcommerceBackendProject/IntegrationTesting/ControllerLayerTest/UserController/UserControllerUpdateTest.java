package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.UserController;

import com.example.EcommerceBackendProject.Controller.UserController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.UserTestFactory;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        updatedUser.setAddresses(List.of());

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

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userService.updateUser(eq(1L), any())).thenReturn(updatedUser);

        ArgumentCaptor<UserUpdateRequestDTO> captor = ArgumentCaptor.forClass(UserUpdateRequestDTO.class);

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

        verify(userService).updateUser(eq(1L), captor.capture());
        UserUpdateRequestDTO captured = captor.getValue();
        assertEquals("testuserupdate", captured.getUsername());
        assertEquals("userupdate", captured.getFirstName());
        assertEquals("lastupdate", captured.getLastName());
        assertEquals("user1update@gmail.com", captured.getEmail());
        assertEquals("+1234567891", captured.getPhoneNumber());
        assertEquals(2, captured.getAddress().size());
    }

    @Test
    void updateUser_failed_nullUsername() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        null,
                        "userupdate",
                        "lastupdate",
                        "user1update@gmail.com",
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("username: must not be blank"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_nullFirstName() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        null,
                        "lastupdate",
                        "user1update@gmail.com",
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("firstName: must not be blank"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_nullLastName() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "user",
                        "userupdate",
                        null,
                        "user1update@gmail.com",
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("lastName: must not be blank"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_emptyAddress() throws Exception {
        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user1update@gmail.com",
                        "+1234567891",
                        List.of()
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("address: must not be empty"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_nullPhoneNumber() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        "userupdate",
                        "lastupdate",
                        "user1update@gmail.com",
                        null,
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("phoneNumber: must not be blank"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_nullEmail() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        "userupdate",
                        "lastupdate",
                        null,
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("email: must not be blank"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_invalidEmailFormat() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "user",
                        "userupdate",
                        "lastupdate",
                        "user1updategmail.com",
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("email: must be a well-formed email address"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_invalidAddress() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(null, null, null, null, null, null);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_notAuthenticate() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("No authentication"));

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("No authentication"));

        verify(securityUtils).getCurrentUserId();
        verifyNoInteractions(userService);
    }

    @Test
    void updateUser_failed_unsupportedMedia() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(put(USER_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Content-Type 'text/plain' is not supported"));

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_emptyBody() throws Exception {
        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_malformedJson() throws Exception {
        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void updateUser_failed_noUserFound() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserUpdateRequestDTO userUpdateRequestDTO =
                UserTestFactory.createUpdateDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userService.updateUser(eq(1L), any())).thenThrow(new NoResourceFoundException("User not found"));

        mockMvc.perform(put(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());

    }
}
