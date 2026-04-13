package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AdminAddressController;

import com.example.EcommerceBackendProject.Controller.AdminAddressController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.Config.MethodSecurityConfig;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAddressController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MethodSecurityConfig.class)
public class AdminAddressControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PageableSortValidator pageableSortValidator;

    private static final String BASE_URL = "/v1/admin/addresses";
    private static final String ADDRESS_URL = BASE_URL + "/{addressId}";

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAnyAddress_success() throws Exception {
        mockMvc.perform(delete(ADDRESS_URL, 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(addressService, times(1)).deleteAnyAddress(1L);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteAnyAddress_failed_nonAdminUser() throws Exception {
        mockMvc.perform(delete(ADDRESS_URL, 1L))
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
    void deleteAnyAddress_failed_invalidPathVariable() throws Exception {
        mockMvc.perform(delete(ADDRESS_URL, "abc"))
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
    void deleteAnyAddress_failed_missingPathVariable() throws Exception {
        mockMvc.perform(delete(ADDRESS_URL, ""))
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
    void deleteAnyAddress_failed_addressNotFound() throws Exception {
        doThrow(new NoResourceFoundException("Address not found")).when(addressService).deleteAnyAddress(1L);

        mockMvc.perform(delete(ADDRESS_URL, "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/1"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Address not found"));

        verify(addressService).deleteAnyAddress(eq(1L));
    }
}
