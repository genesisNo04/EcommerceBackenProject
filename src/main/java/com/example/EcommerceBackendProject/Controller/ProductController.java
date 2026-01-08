package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductResponseDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Mapper.ProductMapper;
import com.example.EcommerceBackendProject.Service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        Product product = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toDTO(product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDTO productRequestDTO) {
        Product product = productService.updateProduct(productRequestDTO, productId);
        return ResponseEntity.ok(ProductMapper.toDTO(product));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/price")
    public ResponseEntity<Map<String, BigDecimal>> getProductPrice(@PathVariable Long productId) {
        BigDecimal productPrice = productService.findProductPrice(productId);
        return ResponseEntity.ok(Map.of("price", productPrice));
    }

    @GetMapping("/{productId}/quantity")
    public ResponseEntity<Map<String, Integer>> getProductQuantity(@PathVariable Long productId) {
        Integer productQuantity = productService.findProductQuantityWithProductId(productId);
        return ResponseEntity.ok(Map.of("quantity", productQuantity));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findProductByKeyword(@RequestParam String keyword
            , @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Product> products = productService.findProductByKeyword(keyword, pageable);
        Page<ProductResponseDTO> productResponse = products.map(ProductMapper::toDTO);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ProductResponseDTO>> findProductByCategory(@RequestParam Category category,
                                                                         @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Product> products = productService.findProductByCategory(category, pageable);
        Page<ProductResponseDTO> productResponse = products.map(ProductMapper::toDTO);
        return ResponseEntity.ok(productResponse);
    }
}
