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
public class ShoppingCartItemServiceAddItemTest {

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void addItemToCart_success() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(1, savedCart.getItems().size());
        assertEquals(2, savedCart.getItems().stream().toList().getFirst().getQuantity());
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).contains(item.getId()));
        assertTrue(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
    }

    @Test
    void addItemToCart_success_existingItem() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(1, savedCart.getItems().size());
        assertEquals(4, savedCart.getItems().stream().toList().getFirst().getQuantity());
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).contains(item.getId()));
        assertTrue(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
    }

    @Test
    void addItemToCart_success_existingItem_exactStockLimit() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO1 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 8);

        shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO1, user.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(1, savedCart.getItems().size());
        assertEquals(10, savedCart.getItems().stream().toList().getFirst().getQuantity());
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).contains(item.getId()));
        assertTrue(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
    }

    @Test
    void addItemToCart_failed_existingItem_insufficientStock() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO1 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 9);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO1, user.getId()));

        assertEquals("Insufficient stock", ex.getMessage());
    }

    @Test
    void addItemToCart_success_multipleItems() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();
        Product product2 = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO1 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product1.getId(), 1);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO2 = ShoppingCartItemTestFactory.createShoppingCartItemDto(product2.getId(), 3);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());
        ShoppingCartItem item1 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO1, user.getId());
        ShoppingCartItem item2 = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO2, user.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(3, savedCart.getItems().size());
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).containsAll(Set.of(item.getId(), item1.getId(), item2.getId())));
        assertTrue(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).containsAll(Set.of(product.getId(), product1.getId(), product2.getId())));
    }

    @Test
    void addItemToCart_failed_invalidQuantity() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), -1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId()));

        assertEquals("Quantity must be greater than zero", ex.getMessage());
    }

    @Test
    void addItemToCart_failed_cartNotFound() {
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(999L, 1);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 999L));

        assertEquals("No cart found", ex.getMessage());
    }

    @Test
    void addItemToCart_failed_noProductFound() {
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(999L, 2);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId()));

        assertEquals("No product found", ex.getMessage());
    }

    @Test
    void addItemToCart_failed_insufficientStock() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 11);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId()));

        assertEquals("Insufficient stock", ex.getMessage());
    }
}
