package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.PaymentService;

import com.example.EcommerceBackendProject.Entity.Payment.PaymentGateway;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentResult;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PaymentServiceSuccessTestConfig {

    @Bean
    public PaymentGateway paymentGatewaySuccess() {
        return request -> PaymentResult.success("TEST-123");
    }
}
