package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AddressController;

import com.example.EcommerceBackendProject.Controller.AddressController;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerDefaultAddressTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private static final String ADDRESS_URL = "/v1/users/addresses/default";

    @Test
    void getDefaultAddress_success() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.getDefaultAddress(1L)).thenReturn(address);

        mockMvc.perform(get(ADDRESS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.street").value("123 Main st"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.state").value("state"))
                .andExpect(jsonPath("$.country").value("country"))
                .andExpect(jsonPath("$.zipCode").value("12345"))
                .andExpect(jsonPath("$.isDefault").value(true));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).getDefaultAddress(1L);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void getDefaultAddress_failed_userNotAuthenticated() throws Exception {
        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("Not authenticated"));

        mockMvc.perform(get(ADDRESS_URL))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Not authenticated"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verifyNoInteractions(addressService);
    }

    @Test
    void getDefaultAddress_failed_userNotFound() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.getDefaultAddress(anyLong())).thenThrow(new NoResourceFoundException("User not found"));

        mockMvc.perform(get(ADDRESS_URL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(addressService).getDefaultAddress(eq(1L));
    }

    @Test
    void getDefaultAddress_failed_noDefaultAddress() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.getDefaultAddress(anyLong())).thenThrow(new NoResourceFoundException("User does not have a default address"));

        mockMvc.perform(get(ADDRESS_URL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User does not have a default address"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(addressService).getDefaultAddress(eq(1L));
    }

    @Test
    void setDefaultAddress_success() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(patch("/v1/users/addresses/{addressId}/default", 2L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).setDefaultAddress(2L, 1L);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void setDefaultAddress_failed_userNotAuthenticated() throws Exception {
        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("Not authenticated"));

        mockMvc.perform(patch("/v1/users/addresses/{addressId}/default", 2L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Not authenticated"))
                .andExpect(jsonPath("$.path").value("/v1/users/addresses/2/default"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verifyNoInteractions(addressService);
    }

    @Test
    void setDefaultAddress_failed_userNotFound() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        doThrow(new NoResourceFoundException("User not found")).when(addressService).setDefaultAddress(anyLong(), anyLong());

        mockMvc.perform(patch("/v1/users/addresses/{addressId}/default", 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value("/v1/users/addresses/2/default"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(addressService).setDefaultAddress(2L, 1L);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void setDefaultAddress_failed_addressNotFound() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        doThrow(new NoResourceFoundException("Address not found")).when(addressService).setDefaultAddress(anyLong(), anyLong());

        mockMvc.perform(patch("/v1/users/addresses/{addressId}/default", 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Address not found"))
                .andExpect(jsonPath("$.path").value("/v1/users/addresses/2/default"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(addressService).setDefaultAddress(2L, 1L);
        verifyNoMoreInteractions(addressService);
    }
}
