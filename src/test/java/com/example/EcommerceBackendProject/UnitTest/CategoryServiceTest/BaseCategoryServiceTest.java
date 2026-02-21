package com.example.EcommerceBackendProject.UnitTest.CategoryServiceTest;

import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BaseCategoryServiceTest {

    @InjectMocks
    public CategoryServiceImpl categoryService;

    @Mock
    public CategoryRepository categoryRepository;

    @Mock
    public ProductRepository productRepository;
}
