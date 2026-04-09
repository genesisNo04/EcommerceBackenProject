package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AddressController;

import com.example.EcommerceBackendProject.Controller.AddressController;
import com.example.EcommerceBackendProject.DTO.AddressResponseDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
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
        address1.setId(2L);
        Address address2 = new Address(user, "1235 Main st", "city", "state", "country", "12345", false);
        address2.setId(3L);
        List<Address> addresses = List.of(address, address1, address2);
        Page<Address> pageAddress = new PageImpl<>(addresses);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.getUserAddresses(eq(1L), any(Pageable.class))).thenReturn(pageAddress);

        mockMvc.perform(get(ADDRESS_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].userId").value(1L))
                .andExpect(jsonPath("$.content[0].street").value("123 Main st"))
                .andExpect(jsonPath("$.content[0].city").value("city"))
                .andExpect(jsonPath("$.content[0].state").value("state"))
                .andExpect(jsonPath("$.content[0].country").value("country"))
                .andExpect(jsonPath("$.content[0].zipCode").value("12345"))
                .andExpect(jsonPath("$.content[0].isDefault").value(true));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).getUserAddresses(eq(1L), any(Pageable.class));
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void getUserAddress_success_pagination() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);
        Address address1 = new Address(user, "1234 Main st", "city", "state", "country", "12345", false);
        address1.setId(2L);
        Address address2 = new Address(user, "1235 Main st", "city", "state", "country", "12345", false);
        address2.setId(3L);
        List<Address> addresses = List.of(address, address1, address2);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.getUserAddresses(eq(1L), any(Pageable.class)))
                .thenAnswer(inv -> {
                    Pageable pageable = inv.getArgument(1);
                    int page = pageable.getPageNumber();
                    int size = pageable.getPageSize();

                    int start = (int) pageable.getOffset();
                    int end = Math.min(start + size, addresses.size());

                    List<Address> subList = addresses.subList(start, end);

                    return new PageImpl<>(subList, pageable, addresses.size());
                });

        mockMvc.perform(get(ADDRESS_URL)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].userId").value(1L))
                .andExpect(jsonPath("$.content[0].street").value("123 Main st"))
                .andExpect(jsonPath("$.content[0].city").value("city"))
                .andExpect(jsonPath("$.content[0].state").value("state"))
                .andExpect(jsonPath("$.content[0].country").value("country"))
                .andExpect(jsonPath("$.content[0].zipCode").value("12345"))
                .andExpect(jsonPath("$.content[0].isDefault").value(true));

        mockMvc.perform(get(ADDRESS_URL)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].userId").value(1L))
                .andExpect(jsonPath("$.content[0].street").value("1235 Main st"))
                .andExpect(jsonPath("$.content[0].city").value("city"))
                .andExpect(jsonPath("$.content[0].state").value("state"))
                .andExpect(jsonPath("$.content[0].country").value("country"))
                .andExpect(jsonPath("$.content[0].zipCode").value("12345"))
                .andExpect(jsonPath("$.content[0].isDefault").value(false));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).getUserAddresses(eq(1L), any(Pageable.class));
        verifyNoMoreInteractions(addressService);
    }
}
