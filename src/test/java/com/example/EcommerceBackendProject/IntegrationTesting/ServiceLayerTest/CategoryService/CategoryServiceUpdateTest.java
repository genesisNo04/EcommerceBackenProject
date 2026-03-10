package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.CategoryService;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceUpdateTest {

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
    void updateCategory_success() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        CategoryRequestDTO categoryUpdateRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of(product1.getId()));

        Category updatedCategory = categoryService.updateCategory(createdCategory.getId(),categoryUpdateRequestDTO);

        Category savedCategory = categoryRepository.findById(updatedCategory.getId()).orElseThrow();
        Product saveProduct = productRepository.findById(product.getId()).orElseThrow();
        Product saveProduct1 = productRepository.findById(product1.getId()).orElseThrow();

        assertEquals("ELECTRONICUpdate", savedCategory.getName());
        assertEquals("ElectronicUpdate", savedCategory.getDescription());
        assertTrue(savedCategory.getProducts().stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertFalse(savedCategory.getProducts().stream().anyMatch(p -> p.getId().equals(product.getId())));
        assertTrue(saveProduct1.getCategories().stream().anyMatch(c -> c.getId().equals(savedCategory.getId())));
        assertFalse(saveProduct.getCategories().stream().anyMatch(c -> c.getId().equals(savedCategory.getId())));
    }

    @Test
    void updateCategory_failed_categoryNotFound() {
        CategoryRequestDTO categoryUpdateRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of());

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.updateCategory(999L,categoryUpdateRequestDTO));

        assertEquals("No Category with id: " + 999L, ex.getMessage());
    }

    @Test
    void updateCategory_failed_duplicateName() {
        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of());
        CategoryRequestDTO categoryRequestDTO1 = CategoryTestFactory.createCategoryDTO("HOME", "Home", Set.of());

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);
        categoryService.createCategory(categoryRequestDTO1);

        CategoryRequestDTO categoryUpdateRequestDTO = CategoryTestFactory.createCategoryDTO("HOME", "ElectronicUpdate", Set.of());

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.updateCategory(createdCategory.getId(),categoryUpdateRequestDTO));

        assertEquals("Category already exist with name: " + categoryRequestDTO1.getName(), ex.getMessage());
    }

    @Test
    void updateCategory_failed_noProductFound() {
        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of());

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        CategoryRequestDTO categoryUpdateRequestDTO = CategoryTestFactory.createCategoryDTO("HOME", "ElectronicUpdate", Set.of(999L));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.updateCategory(createdCategory.getId(), categoryUpdateRequestDTO));

        assertEquals("No product with id: " + 999L, ex.getMessage());
    }

    @Test
    void patchCategory_success() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();

        CategoryRequestDTO categoryRequestDTO = CategoryTestFactory.createCategoryDTO("ELECTRONIC", "Electronic", Set.of(product.getId()));

        Category createdCategory = categoryService.createCategory(categoryRequestDTO);

        CategoryUpdateRequestDTO categoryUpdateRequestDTO = CategoryTestFactory.createUpdateCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of(product1.getId()));

        Category updatedCategory = categoryService.patchCategory(createdCategory.getId(),categoryUpdateRequestDTO);

        Category savedCategory = categoryRepository.findById(updatedCategory.getId()).orElseThrow();
        Product saveProduct = productRepository.findById(product.getId()).orElseThrow();
        Product saveProduct1 = productRepository.findById(product1.getId()).orElseThrow();

        assertEquals("ELECTRONICUpdate", savedCategory.getName());
        assertEquals("ElectronicUpdate", savedCategory.getDescription());
        assertTrue(savedCategory.getProducts().stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertFalse(savedCategory.getProducts().stream().anyMatch(p -> p.getId().equals(product.getId())));
        assertTrue(saveProduct1.getCategories().stream().anyMatch(c -> c.getId().equals(savedCategory.getId())));
        assertFalse(saveProduct.getCategories().stream().anyMatch(c -> c.getId().equals(savedCategory.getId())));
    }
}
