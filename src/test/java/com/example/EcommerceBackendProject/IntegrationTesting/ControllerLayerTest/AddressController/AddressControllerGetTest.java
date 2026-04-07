package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AddressController;

import com.example.EcommerceBackendProject.Controller.AddressController;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Security.JwtService;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerGetTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private JwtService jwtService;

    private static final String ADDRESS_URL = "/v1/users/addresses";

    @Test
    void getUserAddress_success() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);
        Address address1 = new Address(user, "1234 Main st", "city", "state", "country", "12345", false);
        address.setId(2L);
        Address address2 = new Address(user, "1235 Main st", "city", "state", "country", "12345", false);
        address.setId(3L);
        List<Address> addresses = List.of(address, address1, address2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(addresses, pageable, addresses.size());

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.getUserAddresses(1L, pageable)).thenReturn(addressPage);

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
}
