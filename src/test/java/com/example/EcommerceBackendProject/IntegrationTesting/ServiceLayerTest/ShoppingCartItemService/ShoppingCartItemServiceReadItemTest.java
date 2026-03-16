package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ShoppingCartItemService;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.ShoppingCartItemTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.ShoppingCartRepository;
import com.example.EcommerceBackendProject.Service.ShoppingCartItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShoppingCartItemServiceReadItemTest {

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void findItemsByUser_success() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();
        Product product2 = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO1 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product1.getId(), 3);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO2 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product2.getId(), 1);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());
        ShoppingCartItem item1 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO1, user.getId());
        ShoppingCartItem item2 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO2, user.getId());

        Pageable pageable = PageRequest.of(0, 10);

        Page<ShoppingCartItem> returnItems = shoppingCartItemService.findItemsByUser(user.getId(), pageable);

        assertEquals(3, returnItems.getContent().size());
        assertEquals(3, returnItems.getTotalElements());
        assertTrue(returnItems.getContent().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).containsAll(Set.of(item.getId(), item1.getId(), item2.getId())));
    }

    @Test
    void findItemsByUser_success_pagination() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();
        Product product2 = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO1 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product1.getId(), 3);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO2 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product2.getId(), 1);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());
        ShoppingCartItem item1 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO1, user.getId());
        ShoppingCartItem item2 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO2, user.getId());

        Pageable pageable = PageRequest.of(0, 2);

        Page<ShoppingCartItem> returnItems = shoppingCartItemService.findItemsByUser(user.getId(), pageable);

        assertEquals(2, returnItems.getContent().size());
        assertEquals(3, returnItems.getTotalElements());
    }

    @Test
    void findItemsByUser_failed_cartNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.findItemsByUser(999L, pageable));

        assertEquals("No cart found", ex.getMessage());
    }

    @Test
    void findItemByUserAndProduct_success() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();
        Product product2 = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO1 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product1.getId(), 3);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO2 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product2.getId(), 1);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());
        ShoppingCartItem item1 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO1, user.getId());
        ShoppingCartItem item2 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO2, user.getId());

        ShoppingCartItem returnItem = shoppingCartItemService.findItemByUserAndProduct(product.getId(), user.getId());

        assertEquals(item.getId(), returnItem.getId());
    }

    @Test
    void findItemByUserAndProduct_failed_noCartFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.findItemByUserAndProduct(123L, 999L));

        assertEquals("No cart found", ex.getMessage());
    }

    @Test
    void findItemByUserAndProduct_failed_noItemFound() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.findItemByUserAndProduct(999L, user.getId()));

        assertEquals("No item found", ex.getMessage());
    }
}
