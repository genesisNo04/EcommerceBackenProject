package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AddressController;

import com.example.EcommerceBackendProject.Controller.AddressController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerPatchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private static final String ADDRESS_URL = "/v1/users/addresses/1";
    private static final MediaType mediaType = MediaType.APPLICATION_JSON;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void patchAddress_success() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        ArgumentCaptor<AddressUpdateRequestDTO> captor = ArgumentCaptor.forClass(AddressUpdateRequestDTO.class);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.patchAddress(eq(1L), any(AddressUpdateRequestDTO.class), eq(1L))).thenReturn(address);

        mockMvc.perform(patch(ADDRESS_URL)
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

        verify(securityUtils).getCurrentUserId();
        verify(addressService).patchAddress(eq(1L), captor.capture(), eq(1L));
        AddressUpdateRequestDTO captured = captor.getValue();
        assertEquals("123 Main st", captured.getStreet());
        assertEquals("city", captured.getCity());
        assertEquals("state", captured.getState());
        assertEquals("country", captured.getCountry());
        assertEquals("12345", captured.getZipCode());
        assertTrue(captured.getIsDefault());
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void patchAddress_success_partiallyUpdate() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "cityupdate", "state", "countryupdate", "54321", true);
        address.setId(1L);
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress(null, "cityupdate", null, "countryupdate", "54321", null);

        ArgumentCaptor<AddressUpdateRequestDTO> captor = ArgumentCaptor.forClass(AddressUpdateRequestDTO.class);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.patchAddress(eq(1L), any(AddressUpdateRequestDTO.class), eq(1L))).thenReturn(address);

        mockMvc.perform(patch(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.street").value("123 Main st"))
                .andExpect(jsonPath("$.city").value("cityupdate"))
                .andExpect(jsonPath("$.state").value("state"))
                .andExpect(jsonPath("$.country").value("countryupdate"))
                .andExpect(jsonPath("$.zipCode").value("54321"))
                .andExpect(jsonPath("$.isDefault").value(true));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).patchAddress(eq(1L), captor.capture(), eq(1L));
        AddressUpdateRequestDTO captured = captor.getValue();
        assertNull(captured.getStreet());
        assertEquals("cityupdate", captured.getCity());
        assertNull(captured.getState());
        assertEquals("countryupdate", captured.getCountry());
        assertEquals("54321", captured.getZipCode());
        assertNull(captured.getIsDefault());
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void patchAddress_failed_malformedZipCode() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12asd512", true);

        mockMvc.perform(patch(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("zipCode: Zipcode must be 5 digits"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void patchAddress_failed_unsupportedMediaType() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);

        mockMvc.perform(patch(ADDRESS_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.message").value("Content-Type 'text/plain' is not supported"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void patchAddress_failed_invalidJson() throws Exception {
        mockMvc.perform(patch(ADDRESS_URL)
                        .contentType(mediaType)
                        .content("{**JSON**}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void patchAddress_failed_emptyBody() throws Exception {
        mockMvc.perform(patch(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void patchAddress_failed_userNotAuthenticated() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);
        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("No authentication"));

        mockMvc.perform(patch(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("No authentication"));

        verify(securityUtils).getCurrentUserId();
        verifyNoInteractions(addressService);
    }

    @Test
    void patchAddress_failed_userNotFound() throws Exception {
        AddressUpdateRequestDTO addressRequestDTO = AddressTestFactory.createUpdateAddress("123 Main st", "city", "state", "country", "12345", true);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.patchAddress(eq(1L), any(), eq(1L))).thenThrow(new NoResourceFoundException("User not found"));

        mockMvc.perform(patch(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).patchAddress(eq(1L), any(), eq(1L));
        verifyNoMoreInteractions(addressService);
    }
}
