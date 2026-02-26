package com.example.EcommerceBackendProject.UnitTest.PaymentServiceTest;

import com.example.EcommerceBackendProject.Repository.PaymentRepository;
import com.example.EcommerceBackendProject.Service.PaymentService;
import com.example.EcommerceBackendProject.Service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BasePaymentServiceTest {

    @InjectMocks
    public PaymentServiceImpl paymentService;

    @Mock
    public PaymentRepository paymentRepository;
}
