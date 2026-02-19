package com.example.EcommerceBackendProject.UnitTest.ProductServiceTest;

import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Service.impl.ProductServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BaseProductServiceTest {

    @InjectMocks
    public ProductServiceImpl productService;

    @Mock
    public ProductRepository productRepository;

    @Mock
    public CategoryService categoryService;
}
