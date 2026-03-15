package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ShoppingCartItemService;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(3, savedCart.getItems().stream().toList().get(0).getQuantity());
        assertTrue(savedCart.getItems().stream().map(ShoppingCartItem::getId).collect(Collectors.toSet()).containsAll(Set.of(item.getId())));
        assertTrue(savedCart.getItems().stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet()).contains(product.getId()));
    }
}
