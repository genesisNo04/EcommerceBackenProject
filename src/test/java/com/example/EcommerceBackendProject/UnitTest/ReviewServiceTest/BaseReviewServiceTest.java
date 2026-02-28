package com.example.EcommerceBackendProject.UnitTest.ReviewServiceTest;

import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ReviewRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BaseReviewServiceTest {

    @InjectMocks
    public ReviewServiceImpl reviewService;

    @Mock
    public ReviewRepository reviewRepository;

    @Mock
    public ProductRepository productRepository;

    @Mock
    public UserRepository userRepository;
}
