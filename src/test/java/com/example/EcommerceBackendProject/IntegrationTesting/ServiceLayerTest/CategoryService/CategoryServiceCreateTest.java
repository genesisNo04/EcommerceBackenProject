package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.CategoryService;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.CategoryTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceCreateTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void createCategory_success() {
        Product product = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        assertEquals("ELECTRONIC", createdCategory.getName());
        assertEquals("Electronic", createdCategory.getDescription());
        assertTrue(createdCategory.getProducts().stream().anyMatch(p -> p.getId().equals(product.getId())));

        Category savedCategory = categoryRepository.findById(createdCategory.getId()).orElseThrow();

        assertEquals("ELECTRONIC", savedCategory.getName());
        assertEquals("Electronic", savedCategory.getDescription());
        assertTrue(savedCategory.getProducts().stream().anyMatch(p -> p.getId().equals(product.getId())));
    }

    @Test
    void createCategory_attachMultipleProduct() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();
        Product product2 = testDataHelper.createProduct();
        Product product3 = testDataHelper.createProduct();
        Set<Long> ids = Set.of(product.getId(),
                product1.getId(), product2.getId(), product3.getId());

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", ids);

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        assertEquals("ELECTRONIC", createdCategory.getName());
        assertEquals("Electronic", createdCategory.getDescription());
        assertTrue(createdCategory.getProducts().stream().map(Product::getId).collect(Collectors.toSet()).containsAll(ids));

        Category savedCategory = categoryRepository.findById(createdCategory.getId()).orElseThrow();

        assertEquals("ELECTRONIC", savedCategory.getName());
        assertEquals("Electronic", savedCategory.getDescription());
        assertTrue(savedCategory.getProducts().stream().map(Product::getId).collect(Collectors.toSet()).containsAll(ids));
    }
}
