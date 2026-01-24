package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemResponseDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartResponseDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Mapper.ShoppingCartItemMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users/carts")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public ResponseEntity<ShoppingCartResponseDTO> getUserShoppingCart() {
        Long userId = SecurityUtils.getCurrentUserId();
        ShoppingCart shoppingCart = shoppingCartService.findByUserId(userId);
        Set<ShoppingCartItemResponseDTO> items = shoppingCart.getItems().stream().map(ShoppingCartItemMapper::toDTO).collect(Collectors.toSet());
        ShoppingCartResponseDTO shoppingCartResponseDTO = new ShoppingCartResponseDTO();
        shoppingCartResponseDTO.setCartId(shoppingCart.getId());
        shoppingCartResponseDTO.setUserId(userId);
        shoppingCartResponseDTO.setItems(items);
        BigDecimal totalPrice = items.stream()
                .map(i -> i.getPriceSnapshot().multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        shoppingCartResponseDTO.setTotalAmount(totalPrice);
        return ResponseEntity.ok(shoppingCartResponseDTO);
    }

    @PatchMapping
    public ResponseEntity<Void> clearCart() {
        Long userId = SecurityUtils.getCurrentUserId();
        shoppingCartService.clearShoppingCart(userId);
        return ResponseEntity.noContent().build();
    }
}
