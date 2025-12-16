package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByProductId(Long productId);

    List<OrderItem> findByOrderId(Long id);

    List<OrderItem> findByOrderIdIn(List<Long> orderIds);

    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId")
    List<OrderItem> findByUserId(Long orderId);
}
