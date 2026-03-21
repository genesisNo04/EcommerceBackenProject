package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.PaymentService;

import com.example.EcommerceBackendProject.Entity.Payment.PaymentGateway;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentResult;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PaymentServiceFailedTestConfig {

    @Bean
    public PaymentGateway paymentGatewayFailed() {
        return request -> PaymentResult.failed();
    }
}
