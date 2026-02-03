package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemResponseDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartResponseDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Mapper.ShoppingCartItemMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/carts")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    private Long resolveUserId(Long requestedUserId) {
        if (requestedUserId != null) {
            SecurityUtils.requireAdmin();
            return requestedUserId;
        }

        return SecurityUtils.getCurrentUserId();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ShoppingCartResponseDTO> getUserShoppingCart(@RequestParam(required = false) Long requestedUserId) {
        Long userId = resolveUserId(requestedUserId);
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
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> clearCart(@RequestParam(required = false) Long requestedUserId) {
        Long userId = resolveUserId(requestedUserId);
        shoppingCartService.clearShoppingCart(userId);
        return ResponseEntity.noContent().build();
    }
}
