package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.CategoryService;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceReadTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void findCategories_byProductId() {
        Product product = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));
        CategoryRequestDTO categoryRequestDTO1 = CategoryTestFactory.createCategoryDTO("HOME", "Home", Set.of(product.getId()));
        CategoryRequestDTO categoryRequestDTO2 = CategoryTestFactory.createCategoryDTO("KITCHEN", "Kitchen", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);
        Category createdCategory1 = categoryService.createCategory(categoryRequestDTO1);
        Category createdCategory2 = categoryService.createCategory(categoryRequestDTO2);

        List<Category> productCategories = categoryService.findCategoriesByProductId(product.getId());

        assertTrue(productCategories.stream().map(Category::getId).collect(Collectors.toSet()).containsAll(List.of(createdCategory.getId(), createdCategory1.getId(), createdCategory2.getId())));
    }

    @Test
    void findCategories_byProductId_noCategories() {
        Product product = testDataHelper.createProduct();

        List<Category> productCategories = categoryService.findCategoriesByProductId(product.getId());

        assertEquals(0, productCategories.size());
    }

    @Test
    void findCategories_noProductFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.findCategoriesByProductId(999L));

        assertEquals("No product with id: " + 999L, ex.getMessage());
    }

    @Test
    void findCategories_byName() {
        Product product = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        Category savedCategory = categoryService.findByName(createdCategory.getName());

        assertEquals("ELECTRONIC", savedCategory.getName());
        assertEquals("Electronic", savedCategory.getDescription());
        assertTrue(savedCategory.getProducts().stream().anyMatch(p -> p.getId().equals(product.getId())));
    }

    @Test
    void findCategories_byName_noCategoryFound() {

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.findByName("NotFound"));

        assertEquals("No category with name: " + "NotFound", ex.getMessage());
    }
}
