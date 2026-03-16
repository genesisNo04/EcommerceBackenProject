package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ShoppingCartService;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.ShoppingCartItemTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Service.ShoppingCartItemService;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
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
public class ShoppingCartServiceUpdateTest {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void clearShoppingCart_success() {
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

        shoppingCartService.clearShoppingCart(user.getId());

        ShoppingCart cart = shoppingCartService.getCartOrThrow(user.getId());

        assertEquals(0, cart.getItems().size());
    }
}
