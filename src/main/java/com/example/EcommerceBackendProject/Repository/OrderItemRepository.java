package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Page<OrderItem> findByOrderUserId(Long userId, Pageable pageable);
//    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId")
//    List<OrderItem> findByUserId(Long userId);

    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    Optional<OrderItem> findByIdAndOrderIdAndOrderUserId(Long itemId, Long orderId, Long userId);
}
