package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductName(String productName);

    boolean existsByProductId(Long productId);

//    @Query("SELECT p.stockQuantity FROM Product p where p.productName = :productName")
//    Optional<Integer> getStockByProductName(String productName);

    Optional<Integer> findStockQuantityByProductId(Long productId);

    Optional<Product> findByProductId(Long productId);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
}
