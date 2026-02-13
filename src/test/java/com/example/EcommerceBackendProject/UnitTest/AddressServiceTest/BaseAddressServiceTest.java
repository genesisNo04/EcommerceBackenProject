package com.example.EcommerceBackendProject.UnitTest.AddressServiceTest;

import com.example.EcommerceBackendProject.Repository.AddressRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.impl.AddressServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BaseAddressServiceTest {

    @Mock
    public UserRepository userRepository;

    @Mock
    public SecurityUtils securityUtils;

    @Mock
    public AddressRepository addressRepository;

    @InjectMocks
    public AddressServiceImpl addressService;

}
