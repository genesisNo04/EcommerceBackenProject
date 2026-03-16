package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ShoppingCartItemService;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShoppingCartItemServiceUpdateItemTest {

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void updateItemQuantity_success() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(3);

        shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, user.getId(), product.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(1, savedCart.getItems().size());
        assertEquals(3, savedCart.getItems().stream().toList().getFirst().getQuantity());
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).contains(item.getId()));
        assertTrue(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
    }

    @Test
    void updateItemQuantity_success_sameQuantity() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(2);

        shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, user.getId(), product.getId());

        ShoppingCart savedCart = shoppingCartRepository.findById(user.getCart().getId()).orElseThrow();

        assertEquals(1, savedCart.getItems().size());
        assertEquals(2, savedCart.getItems().stream().toList().getFirst().getQuantity());
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).contains(item.getId()));
        assertTrue(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
    }

    @Test
    void updateItemQuantity_failed_invalidQuantity() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(-1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, user.getId(), product.getId()));

        assertEquals("Quantity must be greater than zero", ex.getMessage());
    }

    @Test
    void updateItemQuantity_failed_noCartFound() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(1);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 999L, product.getId()));

        assertEquals("No cart found", ex.getMessage());
    }

    @Test
    void updateItemQuantity_failed_noProductFound() {
        User user = testDataHelper.createUser();

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(1);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, user.getId(), 999L));

        assertEquals("No product found", ex.getMessage());
    }

    @Test
    void updateItemQuantity_failed_insufficientStock() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemDto(product.getId(), 2);

        shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, user.getId());

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(11);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, user.getId(), product.getId()));

        assertEquals("Insufficient stock", ex.getMessage());
    }

    @Test
    void updateItemQuantity_failed_noItemFound() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(9);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, user.getId(), product.getId()));

        assertEquals("No item found", ex.getMessage());
    }

    @Test
    void updateItemQuantity_failed_updateItemInCartOfAnotherUser() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();
        User user1 = testDataHelper.createUser("testuser1", "test", "test1@gmail.com");

        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = ShoppingCartItemTestFactory.createShoppingCartItemUpdateDto(9);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, user1.getId(), product.getId()));

        assertEquals("No item found", ex.getMessage());
    }
}
