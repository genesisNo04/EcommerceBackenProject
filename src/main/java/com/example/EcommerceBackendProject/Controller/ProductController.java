package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductResponseDTO;
import com.example.EcommerceBackendProject.DTO.ProductUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.ProductMapper;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Service.ProductService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final PageableSortValidator pageableSortValidator;

    public ProductController(ProductService productService, CategoryService categoryService, PageableSortValidator pageableSortValidator) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        Product product = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toDTO(product));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        Product product = productService.updateProduct(productRequestDTO, productId);
        return ResponseEntity.ok(ProductMapper.toDTO(product));
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> partiallyUpdateProduct(@PathVariable Long productId, @Valid @RequestBody ProductUpdateRequestDTO productUpdateRequestDTO) {
        Product product = productService.patchProduct(productUpdateRequestDTO, productId);
        return ResponseEntity.ok(ProductMapper.toDTO(product));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/price")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, BigDecimal>> getProductPrice(@PathVariable Long productId) {
        BigDecimal productPrice = productService.findProductPrice(productId);
        return ResponseEntity.ok(Map.of("price", productPrice));
    }

    @GetMapping("/{productId}/quantity")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Integer>> getProductQuantity(@PathVariable Long productId) {
        Integer productQuantity = productService.findProductQuantityWithProductId(productId);
        return ResponseEntity.ok(Map.of("quantity", productQuantity));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductResponseDTO>> findProducts(@RequestParam(required = false) String keyword
            , @PageableDefault(size = 10, sort = {"productName", "id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        pageable = pageableSortValidator.validate(pageable, SortableFields.PRODUCT.getFields());
        Page<Product> products = (keyword == null)
                ? productService.findAll(pageable)
                : productService.findProductByKeyword(keyword, pageable);
        Page<ProductResponseDTO> productResponse = products.map(ProductMapper::toDTO);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/category")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductResponseDTO>> findProductByCategory(@RequestParam String categoryName,
                                                                         @PageableDefault(size = 10, sort = {"productName", "id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Category category = categoryService.findByName(categoryName);
        pageable = pageableSortValidator.validate(pageable, SortableFields.PRODUCT.getFields());
        Page<Product> products = productService.findProductByCategory(category, pageable);
        Page<ProductResponseDTO> productResponse = products.map(ProductMapper::toDTO);
        return ResponseEntity.ok(productResponse);
    }
}
