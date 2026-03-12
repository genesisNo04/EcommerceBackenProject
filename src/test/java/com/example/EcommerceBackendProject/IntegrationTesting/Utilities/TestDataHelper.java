package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Service.ProductService;
import com.example.EcommerceBackendProject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class TestDataHelper {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    public User createUser() {
        UserRequestDTO dto = UserTestFactory.createTestUser(
                "testuser",
                "test123",
                "testuser@gmail.com",
                "test",
                "user",
                "+12345678981",
                List.of()
        );
        return userService.createCustomerUser(dto);
    }

    public User createUser(String username, String password, String email) {
        UserRequestDTO dto = UserTestFactory.createTestUser(
                username,
                password,
                email,
                "test",
                "user",
                "+12345678981",
                List.of()
        );
        return userService.createCustomerUser(dto);
    }

    public User createUser(String username, String password, String email, String firstName, String lastName, String phoneNumber, List<AddressRequestDTO> addresses) {
        UserRequestDTO dto = UserTestFactory.createTestUser(
                username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                addresses
        );
        return userService.createCustomerUser(dto);
    }

    public Product createProduct() {
        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO("test",
                "test",
                10,
                Set.of(),
                BigDecimal.valueOf(29.99),
                "testurl");
        return productService.createProduct(productRequestDTO);
    }

    public Product createProduct(String productName,
                                 String description,
                                 Integer stockQuantity,
                                 Set<Long> categoriesId,
                                 BigDecimal price,
                                 String imageUrl) {
        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO(productName,
                description,
                stockQuantity,
                categoriesId,
                price,
                imageUrl);
        return productService.createProduct(productRequestDTO);
    }

    public Category createCategory() {
        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of());
        return categoryService.createCategory(categoryRequestDTO);
    }

    public Category createCategory(String name,
                                 String description,
                                 Set<Long> productsId) {
        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO(name, description, productsId);
        return categoryService.createCategory(categoryRequestDTO);
    }
}
