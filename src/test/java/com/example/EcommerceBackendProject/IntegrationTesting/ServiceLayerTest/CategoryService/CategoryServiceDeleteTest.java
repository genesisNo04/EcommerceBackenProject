package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.CategoryService;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.CategoryTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceDeleteTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void deleteTest() {
        Product product = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));
        CategoryRequestDTO categoryRequestDTO1 = CategoryTestFactory.createCategoryDTO("HOME", "Home", Set.of(product.getId()));
        CategoryRequestDTO categoryRequestDTO2 = CategoryTestFactory.createCategoryDTO("KITCHEN", "Kitchen", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);
        Category createdCategory1 = categoryService.createCategory(categoryRequestDTO1);
        Category createdCategory2 = categoryService.createCategory(categoryRequestDTO2);

        categoryService.deleteCategory(createdCategory.getId());

        Product savedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(2, savedProduct.getCategories().size());
        assertTrue(savedProduct.getCategories().stream().map(Category::getId).collect(Collectors.toSet()).containsAll(List.of(createdCategory1.getId(), createdCategory2.getId())));
        assertFalse(savedProduct.getCategories().stream().map(Category::getId).collect(Collectors.toSet()).contains(createdCategory.getId()));
    }

    @Test
    void deleteTest_categoryNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.deleteCategory(999L));

        assertEquals("No category with id: " + 999L, ex.getMessage());
    }


}
