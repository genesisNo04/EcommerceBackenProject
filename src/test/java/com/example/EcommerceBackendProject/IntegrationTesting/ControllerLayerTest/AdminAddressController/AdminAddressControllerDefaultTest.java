package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AdminAddressController;

import com.example.EcommerceBackendProject.Controller.AdminAddressController;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminAddressControllerDefaultTest {

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

    private static final String ADDRESS_URL = "/v1/admin/addresses";

    @Test
    void getDefaultAddress_success() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);

        when(addressService.getDefaultAddress(1L)).thenReturn(address);

        mockMvc.perform(get(ADDRESS_URL + "/default")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("123 Main st"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.state").value("state"))
                .andExpect(jsonPath("$.country").value("country"))
                .andExpect(jsonPath("$.zipCode").value("12345"))
                .andExpect(jsonPath("$.isDefault").value(true));

        verify(addressService).getDefaultAddress(1L);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void getDefaultAddress_failed_userNotFound() throws Exception {
        when(addressService.getDefaultAddress(1L)).thenThrow(new NoResourceFoundException("User not found"));

        mockMvc.perform(get(ADDRESS_URL + "/default")
                        .param("userId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL + "/default"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(addressService).getDefaultAddress(1L);
    }

    @Test
    void getDefaultAddress_failed_addressNotFound() throws Exception {
        when(addressService.getDefaultAddress(1L)).thenThrow(new NoResourceFoundException("User does not have a default address"));

        mockMvc.perform(get(ADDRESS_URL + "/default")
                        .param("userId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User does not have a default address"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL + "/default"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(addressService).getDefaultAddress(1L);
    }

    @Test
    void getDefaultAddress_failed_invalidParam() throws Exception {
        mockMvc.perform(get(ADDRESS_URL + "/default")
                        .param("userId", "abcd"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("userId")))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL + "/default"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService);
    }

    @Test
    void getDefaultAddress_failed_missingParam() throws Exception {
        mockMvc.perform(get(ADDRESS_URL + "/default"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("userId")))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL + "/default"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService);
    }
}
