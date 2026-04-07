package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AddressController;

import com.example.EcommerceBackendProject.Controller.AddressController;
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
public class AddressControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private static final String ADDRESS_URL = "/v1/users/addresses/{addressId}";

    @Test
    void deleteAddress_success() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(delete(ADDRESS_URL, 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).deleteAddress(1L, 1L);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void deleteAddress_failed_userNotAuthenticated() throws Exception {
        when(securityUtils.getCurrentUserId()).thenThrow(new UserAccessDeniedException("Not authenticated"));

        mockMvc.perform(delete(ADDRESS_URL, 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Not authenticated"))
                .andExpect(jsonPath("$.path").value("/v1/users/addresses/1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verifyNoInteractions(addressService);
    }

    @Test
    void deleteAddress_failed_userNotFound() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        doThrow(new NoResourceFoundException("User not found")).when(addressService).deleteAddress(anyLong(), anyLong());

        mockMvc.perform(delete(ADDRESS_URL, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value("/v1/users/addresses/1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(addressService).deleteAddress(eq(1L), eq(1L));
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void deleteAddress_failed_addressNotFound() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        doThrow(new NoResourceFoundException("Address not found")).when(addressService).deleteAddress(anyLong(), anyLong());

        mockMvc.perform(delete(ADDRESS_URL, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Address not found"))
                .andExpect(jsonPath("$.path").value("/v1/users/addresses/1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(addressService).deleteAddress(eq(1L), eq(1L));
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void deleteAddress_failed_invalidPath() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        doThrow(new NoResourceFoundException("Address not found")).when(addressService).deleteAddress(anyLong(), anyLong());
        String id = "abcd";
        mockMvc.perform(delete(ADDRESS_URL, id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Address not found"))
                .andExpect(jsonPath("$.path").value("/v1/users/addresses/" + id))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(securityUtils).getCurrentUserId();
        verify(addressService).deleteAddress(eq(1L), eq(1L));
        verifyNoMoreInteractions(addressService);
    }

}
