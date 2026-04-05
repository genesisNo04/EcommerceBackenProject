package com.example.EcommerceBackendProject.IntegrationTesting.ControllerLayerTest.AddressController;

import com.example.EcommerceBackendProject.Controller.AddressController;
import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerCreateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private static final String ADDRESS_URL = "/v1/users/addresses";
    private static final MediaType mediaType = MediaType.APPLICATION_JSON;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void createAddress_success() throws Exception {
        User user = new User(null, "+1234567891", "last", "user",
                "encodedPassword", "user1@gmail.com", "testuser");
        user.setId(1L);
        Address address = new Address(user, "123 Main st", "city", "state", "country", "12345", true);
        address.setId(1L);
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "city", "state", "country", "12345", true);

        ArgumentCaptor<AddressRequestDTO> captor = ArgumentCaptor.forClass(AddressRequestDTO.class);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(addressService.createAddress(any(AddressRequestDTO.class), eq(1L))).thenReturn(address);

        mockMvc.perform(post(ADDRESS_URL)
                .contentType(mediaType)
                .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.street").value("123 Main st"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.state").value("state"))
                .andExpect(jsonPath("$.country").value("country"))
                .andExpect(jsonPath("$.zipCode").value("12345"))
                .andExpect(jsonPath("$.isDefault").value(true));

        verify(securityUtils).getCurrentUserId();
        verify(addressService).createAddress(captor.capture(), eq(1L));
        verifyNoMoreInteractions(addressService);
    }

    @Test
    void createAddress_failed_emptyStreet() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("", "city", "state", "country", "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("street: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_nullStreet() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(null, "city", "state", "country", "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("street: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_emptyCity() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "", "state", "country", "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("city: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_nullCity() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", null, "state", "country", "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("city: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_emptyState() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "city", "", "country", "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("state: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_nullState() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "city", null, "country", "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("state: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_emptyCountry() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "city", "state", "", "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("country: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_nullCountry() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "city", "state", null, "12345", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("country: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_emptyZipCode() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "city", "state", "country", "", true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("zipCode: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }

    @Test
    void createAddress_failed_nullZipCode() throws Exception {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "city", "state", "country", null, true);

        mockMvc.perform(post(ADDRESS_URL)
                        .contentType(mediaType)
                        .content(mapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("zipCode: Street cannot be empty"))
                .andExpect(jsonPath("$.path").value(ADDRESS_URL))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(addressService, securityUtils);
    }
}
