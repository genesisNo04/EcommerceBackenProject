package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AdminAddressController;

import com.example.EcommerceBackendProject.Controller.AdminAddressController;

import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.Config.MethodSecurityConfig;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAddressController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MethodSecurityConfig.class)
public class AdminAddressControllerPatchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private PageableSortValidator pageableSortValidator;

    private static final String BASE_URL = "/v1/admin/addresses";
    private static final String ADDRESS_URL = BASE_URL + "/{addressId}";
    private static final MediaType mediaType = MediaType.APPLICATION_JSON;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_success() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        ArgumentCaptor<AddressUpdateRequestDTO> captor = ArgumentCaptor.forClass(AddressUpdateRequestDTO.class);

        when(addressService.patchAnyAddress(eq(1L), any(AddressUpdateRequestDTO.class))).thenReturn(address);

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.street").value("123 Main st"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.state").value("state"))
                .andExpect(jsonPath("$.country").value("country"))
                .andExpect(jsonPath("$.zipCode").value("12345"))
                .andExpect(jsonPath("$.isDefault").value(true));

        verify(addressService).patchAnyAddress(eq(1L), captor.capture());
        AddressUpdateRequestDTO dto = captor.getValue();
        assertEquals("123 Main st", dto.getStreet());
        assertEquals("city", dto.getCity());
        assertEquals("state", dto.getState());
        assertEquals("country", dto.getCountry());
        assertEquals("12345", dto.getZipCode());
        assertTrue(dto.getIsDefault());
        verifyNoMoreInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_success_partiallyUpdate() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", null, "state", null, "12345", null);

        ArgumentCaptor<AddressUpdateRequestDTO> captor = ArgumentCaptor.forClass(AddressUpdateRequestDTO.class);

        when(addressService.patchAnyAddress(eq(1L), any(AddressUpdateRequestDTO.class))).thenReturn(address);

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.street").value("123 Main st"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.state").value("state"))
                .andExpect(jsonPath("$.country").value("country"))
                .andExpect(jsonPath("$.zipCode").value("12345"))
                .andExpect(jsonPath("$.isDefault").value(true));

        verify(addressService).patchAnyAddress(eq(1L), captor.capture());
        AddressUpdateRequestDTO dto = captor.getValue();
        assertEquals("123 Main st", dto.getStreet());
        assertNull(dto.getCity());
        assertEquals("state", dto.getState());
        assertNull(dto.getCountry());
        assertEquals("12345", dto.getZipCode());
        assertNull(dto.getIsDefault());
        verifyNoMoreInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminPatchAddress_noAdminRole() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message", containsString("Access Denied")))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_missingPathVariable() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        mockMvc.perform(patch(ADDRESS_URL, "")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("No handler found")))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_invalidPathVariable() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        mockMvc.perform(patch(ADDRESS_URL, "abc")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("Invalid value")))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/abc"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_malformedZipCode() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12asd512", true);

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("zipCode: Zipcode must be 5 digits"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_unsupportedMediaType() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.message").value("Content-Type 'text/plain' is not supported"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_missingContentType() throws Exception {
        AddressUpdateRequestDTO dto = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_invalidJson() throws Exception {
        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content("{**JSON**}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_emptyBody() throws Exception {
        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_userNotFound() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);
        when(addressService.patchAnyAddress(eq(1L), any())).thenThrow(new NoResourceFoundException("User not found"));

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(addressService).patchAnyAddress(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPatchAddress_failed_addressNotFound() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);
        when(addressService.patchAnyAddress(eq(1L), any())).thenThrow(new NoResourceFoundException("Address not found"));

        mockMvc.perform(patch(ADDRESS_URL, "1")
                        .with(csrf())
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Address not found"));

        verify(addressService).patchAnyAddress(eq(1L), any());
    }
}
