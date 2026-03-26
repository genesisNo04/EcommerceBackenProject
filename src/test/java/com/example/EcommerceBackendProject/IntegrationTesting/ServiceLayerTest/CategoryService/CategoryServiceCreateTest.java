package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.CategoryService;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
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

    @Test
    void createCategory_duplicateName() {
        Product product = testDataHelper.createProduct();
        Set<Long> ids = Set.of(product.getId());

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", ids);

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.createCategory(categoryRequestDTO));

        assertEquals("Category is already existed with this name: " + createdCategory.getName(), ex.getMessage());
    }

    @Test
    void createCategory_productNotFound() {
        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(999L));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.createCategory(categoryRequestDTO));

        assertEquals("One or more products not found" , ex.getMessage());
    }

    @Test
    void resolveCategories() {
        Product product = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));
        CategoryRequestDTO categoryRequestDTO1 = CategoryTestFactory.createCategoryDTO("HOME", "Home", Set.of(product.getId()));
        CategoryRequestDTO categoryRequestDTO2 = CategoryTestFactory.createCategoryDTO("KITCHEN", "Kitchen", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);
        Category createdCategory1 = categoryService.createCategory(categoryRequestDTO1);
        Category createdCategory2 = categoryService.createCategory(categoryRequestDTO2);
        Set<Long> ids = Set.of(createdCategory.getId(), createdCategory1.getId(), createdCategory2.getId());

        Set<Category> categories = categoryService.resolveCategories(ids);

        assertEquals(3, categories.size());
        assertTrue(categories.stream().map(Category::getId).collect(Collectors.toSet()).containsAll(ids));
    }

    @Test
    void resolveCategories_categoriesNotFound() {
        Product product = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));
        CategoryRequestDTO categoryRequestDTO1 = CategoryTestFactory.createCategoryDTO("HOME", "Home", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);
        Category createdCategory1 = categoryService.createCategory(categoryRequestDTO1);
        Set<Long> ids = Set.of(createdCategory.getId(), createdCategory1.getId(), 999L);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.resolveCategories(ids));

        assertEquals("One or more categories is not found", ex.getMessage());
    }
}
