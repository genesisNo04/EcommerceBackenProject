package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    Integer findProductQuantityWithProductId(Long productId);

    Product createProduct(ProductRequestDTO productRequestDTO);

    Product updateProduct(ProductRequestDTO productRequestDTO, Long productId);

    void deleteProduct(Long productId);

    BigDecimal findProductPrice(Long productId);

    Page<Product> findProductByKeyword(String keyword, Pageable pageable);

    Page<Product> findProductByCategory(Category category, Pageable pageable);

    Page<Product> findAll(Pageable pageable);
}
