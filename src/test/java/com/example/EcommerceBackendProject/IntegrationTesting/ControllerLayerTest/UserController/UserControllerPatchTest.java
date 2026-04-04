package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.UserController;

import com.example.EcommerceBackendProject.Controller.UserController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserPatchRequestDTO;
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
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerPatchTest {

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
    void patchUser_success() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        "testuserupdate",
                        "userupdate",
                        "lastupdate",
                        "user1update@gmail.com",
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userService.patchUser(eq(1L), any())).thenReturn(updatedUser);

        ArgumentCaptor<UserPatchRequestDTO> captor = ArgumentCaptor.forClass(UserPatchRequestDTO.class);

        mockMvc.perform(patch(USER_URL)
                .contentType(mediaType)
                .content(mapper.writeValueAsString(userPatchRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuserupdate"))
                .andExpect(jsonPath("$.email").value("user1update@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("userupdate"))
                .andExpect(jsonPath("$.lastName").value("lastupdate"))
                .andExpect(jsonPath("$.address").isArray())
                .andExpect(jsonPath("$.phoneNumber").value("+1234567891"));

        verify(userService).patchUser(eq(1L), captor.capture());
        UserPatchRequestDTO captured = captor.getValue();
        assertEquals("testuserupdate", captured.getUsername());
        assertEquals("userupdate", captured.getFirstName());
        assertEquals("lastupdate", captured.getLastName());
        assertEquals("user1update@gmail.com", captured.getEmail());
        assertEquals("+1234567891", captured.getPhoneNumber());
        assertEquals(2, captured.getAddress().size());
    }

    @Test
    void patchUser_success_partiallyUpdate() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "user",
                "encodedPassword", "user1@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        "testuserupdate",
                        null,
                        "lastupdate",
                        null,
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userService.patchUser(eq(1L), any())).thenReturn(updatedUser);

        ArgumentCaptor<UserPatchRequestDTO> captor = ArgumentCaptor.forClass(UserPatchRequestDTO.class);

        mockMvc.perform(patch(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userPatchRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuserupdate"))
                .andExpect(jsonPath("$.email").value("user1@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("user"))
                .andExpect(jsonPath("$.lastName").value("lastupdate"))
                .andExpect(jsonPath("$.address").isArray())
                .andExpect(jsonPath("$.phoneNumber").value("+1234567891"));

        verify(userService).patchUser(eq(1L), captor.capture());
        UserPatchRequestDTO captured = captor.getValue();
        assertEquals("testuserupdate", captured.getUsername());
        assertNull(captured.getFirstName());
        assertEquals("lastupdate", captured.getLastName());
        assertNull(captured.getEmail());
        assertEquals("+1234567891", captured.getPhoneNumber());
        assertEquals(2, captured.getAddress().size());
    }

    @Test
    void patchUser_success_allNull() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "user",
                "encodedPassword", "user1@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userService.patchUser(eq(1L), any())).thenReturn(updatedUser);

        ArgumentCaptor<UserPatchRequestDTO> captor = ArgumentCaptor.forClass(UserPatchRequestDTO.class);

        mockMvc.perform(patch(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userPatchRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuserupdate"))
                .andExpect(jsonPath("$.email").value("user1@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("user"))
                .andExpect(jsonPath("$.lastName").value("lastupdate"))
                .andExpect(jsonPath("$.address").isArray())
                .andExpect(jsonPath("$.phoneNumber").value("+1234567891"));

        verify(userService).patchUser(eq(1L), captor.capture());
        UserPatchRequestDTO captured = captor.getValue();
        assertNull(captured.getUsername());
        assertNull(captured.getFirstName());
        assertNull(captured.getLastName());
        assertNull(captured.getEmail());
        assertNull(captured.getPhoneNumber());
        assertNull(captured.getAddress());
    }

    @Test
    void patchUser_failed_invalidEmailFormat() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        "user",
                        "userupdate",
                        "lastupdate",
                        "user1updategmail.com",
                        "+1234567891",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(patch(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userPatchRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("email: must be a well-formed email address"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void patchUser_failed_invalidAddress() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(null, null, null, null, null, null);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(patch(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userPatchRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists());
        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void patchUser_failed_notAuthenticate() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("No authentication"));

        mockMvc.perform(patch(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userPatchRequestDTO)))
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
    void patchUser_failed_unsupportedMedia() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        mockMvc.perform(patch(USER_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(mapper.writeValueAsString(userPatchRequestDTO)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Content-Type 'text/plain' is not supported"));

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void patchUser_failed_emptyBody() throws Exception {
        mockMvc.perform(patch(USER_URL)
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
    void patchUser_failed_malformedJson() throws Exception {
        mockMvc.perform(patch(USER_URL)
                        .contentType(mediaType)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService, securityUtils);
    }

    @Test
    void patchUser_failed_noUserFound() throws Exception {
        User updatedUser = new User(null, "+1234567891", "lastupdate", "userupdate",
                "encodedPassword", "user1update@gmail.com", "testuserupdate");
        updatedUser.setId(1L);
        updatedUser.setAddresses(List.of());

        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);

        UserPatchRequestDTO userPatchRequestDTO =
                UserTestFactory.createPatchDTOTestUser(
                        "test",
                        "user",
                        "last",
                        "user@gmail.com",
                        "+1234567890",
                        List.of(addressRequestDTO, addressRequestDTO1)
                );

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userService.patchUser(eq(1L), any())).thenThrow(new NoResourceFoundException("User not found"));

        mockMvc.perform(patch(USER_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(userPatchRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(USER_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());

    }
}
