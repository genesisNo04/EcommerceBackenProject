package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.Service.ProductService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products/{productId}/categories")
public class ProductCategoryController {

    private final ProductService productService;

    public ProductCategoryController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{categoryId}")
    public ResponseEntity<Void> addCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
        productService.addCategory(productId, categoryId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> removeCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
        productService.removeCategory(productId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
