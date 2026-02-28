package com.example.EcommerceBackendProject.UnitTest.CategoryServiceTest;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceUpdateTest extends BaseCategoryServiceTest{

    @Test
    void updateCategory() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.addCategory(category);

        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of(1L));

        when(categoryRepository.existsByName("ELECTRONICUpdate")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Category updateCategory = categoryService.updateCategory(1L, categoryRequestDTO);

        assertEquals("ELECTRONICUpdate", updateCategory.getName());
        assertEquals("ElectronicUpdate", updateCategory.getDescription());
        assertTrue(product.getCategories().contains(updateCategory));

        verify(categoryRepository).findById(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void updateCategory_nameChangeAlreadyExist() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.addCategory(category);

        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of(1L));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("ELECTRONICUpdate")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.updateCategory(1L, categoryRequestDTO));

        assertEquals("Category already exist with name: " + categoryRequestDTO.getName(), ex.getMessage());
        assertTrue(product.getCategories().contains(category));

        InOrder inOrder = inOrder(categoryRepository);
        inOrder.verify(categoryRepository).findById(1L);
        inOrder.verify(categoryRepository).existsByName("ELECTRONICUpdate");
    }

    @Test
    void updateCategory_sameNameNotThrown() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.setId(1L);
        product.addCategory(category);

        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONIC", "ElectronicUpdate", Set.of(1L));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Category updateCategory = categoryService.updateCategory(1L, categoryRequestDTO);

        assertEquals("ELECTRONIC", updateCategory.getName());
        assertEquals("ElectronicUpdate", updateCategory.getDescription());
        assertTrue(product.getCategories().contains(updateCategory));

        verify(categoryRepository, never()).existsByName("ELECTRONIC");
        verify(categoryRepository).findById(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void updateCategory_listProductEmpty() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.addCategory(category);

        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category updateCategory = categoryService.updateCategory(1L, categoryRequestDTO);

        assertEquals("ELECTRONICUpdate", updateCategory.getName());
        assertEquals("ElectronicUpdate", updateCategory.getDescription());
        assertFalse(product.getCategories().contains(updateCategory));

        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void updateCategory_updateListProduct() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);

        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.setId(1L);
        product.addCategory(category);

        Product product1 = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product1.setId(2L);
        product1.addCategory(category);

        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of(1L));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Category updateCategory = categoryService.updateCategory(1L, categoryRequestDTO);

        assertEquals("ElectronicUpdate", updateCategory.getDescription());
        assertTrue(product.getCategories().contains(updateCategory));
        assertFalse(product1.getCategories().contains(updateCategory));

        verify(categoryRepository).findById(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void updateCategory_noCategoryFound() {
        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of(1L));
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.updateCategory(1L, categoryRequestDTO));

        assertEquals("No Category with id: " + 1L, ex.getMessage());

        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void updateCategory_noProductFound() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);

        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONICUpdate", "ElectronicUpdate", Set.of(1L));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.updateCategory(1L, categoryRequestDTO));

        assertEquals("No product with id: " + 1L, ex.getMessage());

        verify(categoryRepository).findById(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void patchCategory() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.addCategory(category);

        CategoryUpdateRequestDTO categoryRequestDTO = createTestUpdateCategoryDTO("ELECTRONICUpdate", null, Set.of(1L));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Category updateCategory = categoryService.patchCategory(1L, categoryRequestDTO);

        assertEquals("ELECTRONICUpdate", updateCategory.getName());
        assertEquals("Electronic", updateCategory.getDescription());
        assertTrue(product.getCategories().contains(updateCategory));

        verify(categoryRepository).findById(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void patchCategory_descriptionOnly() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.addCategory(category);

        CategoryUpdateRequestDTO categoryRequestDTO = createTestUpdateCategoryDTO(null, "ElectronicUpdate", null);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category updateCategory = categoryService.patchCategory(1L, categoryRequestDTO);

        assertEquals("ELECTRONIC", updateCategory.getName());
        assertEquals("ElectronicUpdate", updateCategory.getDescription());
        assertTrue(product.getCategories().contains(updateCategory));

        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void patchCategory_listProductEmpty() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        category.setId(1L);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.addCategory(category);

        CategoryUpdateRequestDTO categoryRequestDTO = createTestUpdateCategoryDTO("ELECTRONICUpdate", null, Set.of());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category updateCategory = categoryService.patchCategory(1L, categoryRequestDTO);

        assertEquals("ELECTRONICUpdate", updateCategory.getName());
        assertEquals("Electronic", updateCategory.getDescription());
        assertFalse(product.getCategories().contains(updateCategory));

        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void patchCategory_noCategoryFound() {
        CategoryUpdateRequestDTO categoryRequestDTO = createTestUpdateCategoryDTO("ELECTRONICUpdate", null, Set.of());

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.patchCategory(1L, categoryRequestDTO));

        assertEquals("No Category with id: " + 1L, ex.getMessage());

        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void patchCategory_noProductFound() {
        CategoryUpdateRequestDTO categoryRequestDTO = createTestUpdateCategoryDTO("ELECTRONICUpdate", null, Set.of(1L));
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.patchCategory(1L, categoryRequestDTO));

        assertEquals("No Product with id: " + 1L, ex.getMessage());

        verify(categoryRepository).findById(1L);
        verify(productRepository).findById(1L);
    }
}
