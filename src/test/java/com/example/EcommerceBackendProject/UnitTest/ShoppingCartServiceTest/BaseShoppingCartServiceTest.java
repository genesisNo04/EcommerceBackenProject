package com.example.EcommerceBackendProject.UnitTest.ShoppingCartServiceTest;

import com.example.EcommerceBackendProject.Repository.ShoppingCartRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BaseShoppingCartServiceTest {

    @InjectMocks
    public ShoppingCartServiceImpl shoppingCartService;

    @Mock
    public ShoppingCartRepository shoppingCartRepository;

    @Mock
    public UserRepository userRepository;
}
