package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ShoppingCartService;

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
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import jakarta.persistence.EntityManager;
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
public class ShoppingCartServiceReadTest {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private EntityManager entityManager;

    @Test
    void getCartOrThrow_success() {
        User user = testDataHelper.createUser();

        ShoppingCart cart = shoppingCartService.getCartOrThrow(user.getId());

        assertEquals(user.getCart().getId(), cart.getId());
    }

    @Test
    void getCartOrThrow_success_withItem() {
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

        ShoppingCart cart = shoppingCartService.getCartOrThrow(user.getId());

        assertEquals(3, cart.getItems().size());
        assertTrue(cart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).containsAll(Set.of(item.getId(), item1.getId(), item2.getId())));
        assertTrue(cart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).containsAll(Set.of(product.getId(), product1.getId(), product2.getId())));
    }


    @Test
    void getCartOrThrow_failed_noCartFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartService.getCartOrThrow(999L));

        assertEquals("No cart found", ex.getMessage());
    }

    @Test
    void findByUserId_success() {
        User user = testDataHelper.createUser();

        ShoppingCart cart = shoppingCartService.findByUserId(user.getId());

        assertEquals(user.getCart().getId(), cart.getId());
    }

    @Test
    void findByUserId_failed_noUserFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartService.findByUserId(999L));

        assertEquals("No user found", ex.getMessage());
    }
}
