package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductName(String productName);

//    @Query("SELECT p.stockQuantity FROM Product p where p.productName = :productName")
//    Optional<Integer> getStockByProductName(String productName);

    @Query("SELECT p.stockQuantity FROM Product p WHERE p.id = :productId")
    Optional<Integer> findStockQuantityByProductId(Long productId);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT p.price FROM Product p WHERE p.id = :productId")
    Optional<BigDecimal> findPriceById(Long productId);

    @Query("SELECT p.categories FROM Product p WHERE p.id = :productId")
    List<Category> findCategoryById(Long productId);

    Page<Product> findByCategories(Category category, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
