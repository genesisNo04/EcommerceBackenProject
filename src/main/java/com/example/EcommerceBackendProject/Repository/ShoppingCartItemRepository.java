package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {

    Page<ShoppingCartItem> findByShoppingCartId(Long shoppingCartId, Pageable pageable);

    Optional<ShoppingCartItem> findByShoppingCartIdAndProductId(Long cartId, Long productId);

    void deleteByShoppingCartId(Long cartId);

    Page<ShoppingCartItem> findByShoppingCartUserId(Long userId, Pageable pageable);

//    @Query("SELECT COUNT(i) FROM ShoppingCartItem i WHERE i.shoppingCart.id = :shoppingCartId")
    long countByShoppingCartId(Long shoppingCartId);
}
