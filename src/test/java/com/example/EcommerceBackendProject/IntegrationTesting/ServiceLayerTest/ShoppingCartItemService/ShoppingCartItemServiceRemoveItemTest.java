package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ShoppingCartItemService;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShoppingCartItemServiceRemoveItemTest {

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void removeItem_success() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        shoppingCartItemService.removeItemFromCart(product.getId(), user.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(0, savedCart.getItems().size());
        assertFalse(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).contains(item.getId()));
        assertFalse(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
    }

    @Test
    void removeItem_success_multipleItemExist() {
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

        shoppingCartItemService.removeItemFromCart(product.getId(), user.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(2, savedCart.getItems().size());
        assertFalse(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).contains(item.getId()));
        assertFalse(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).containsAll(Set.of(item1.getId(), item2.getId())));
    }

    @Test
    void removeItem_failed_cartNotFound() {
        Product product = testDataHelper.createProduct();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.removeItemFromCart(product.getId(), 999L));

        assertEquals("No cart found", ex.getMessage());
    }

    @Test
    void removeItem_failed_noItemFound() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.removeItemFromCart(999L, user.getId()));

        assertEquals("No item found", ex.getMessage());
    }
}
