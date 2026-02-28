package com.example.EcommerceBackendProject.UnitTest.ShoppingCartItemServiceTest;

import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ShoppingCartItemRepository;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import com.example.EcommerceBackendProject.Service.impl.ShoppingCartItemServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BaseShoppingCartItemServiceTest {

    @Mock
    public ShoppingCartItemRepository shoppingCartItemRepository;

    @Mock
    public ShoppingCartService shoppingCartService;

    @Mock
    public ProductRepository productRepository;

    @InjectMocks
    public ShoppingCartItemServiceImpl shoppingCartItemService;
}
