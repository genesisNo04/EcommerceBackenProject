package com.example.EcommerceBackendProject.UnitTest.OrderItemServiceTest;

import com.example.EcommerceBackendProject.Repository.OrderItemRepository;
import com.example.EcommerceBackendProject.Service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BaseOrderItemServiceTest {

    @InjectMocks
    public OrderItemServiceImpl orderItemService;

    @Mock
    public OrderItemRepository orderItemRepository;
}
