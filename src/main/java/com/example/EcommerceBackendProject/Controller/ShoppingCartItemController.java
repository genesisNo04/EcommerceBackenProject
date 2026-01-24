package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemResponseDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.ShoppingCartItemMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.ShoppingCartItemService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/items")
public class ShoppingCartItemController {

    private final ShoppingCartItemService shoppingCartItemService;
    private final PageableSortValidator pageableSortValidator;

    public ShoppingCartItemController(ShoppingCartItemService shoppingCartItemService, PageableSortValidator pageableSortValidator) {
        this.shoppingCartItemService = shoppingCartItemService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @PostMapping
    public ResponseEntity<ShoppingCartItemResponseDTO> addItemToCart(@Valid @RequestBody ShoppingCartItemRequestDTO shoppingCartItemRequestDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        ShoppingCartItem item = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, userId);
        return ResponseEntity.ok(ShoppingCartItemMapper.toDTO(item));
    }

    @PatchMapping("/product/{productId}")
    public ResponseEntity<ShoppingCartItemResponseDTO> updateItemQuantity(@PathVariable Long productId, @Valid @RequestBody ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        ShoppingCartItem item = shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, userId, productId);
        return ResponseEntity.ok(ShoppingCartItemMapper.toDTO(item));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        shoppingCartItemService.removeItemFromCart(productId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ShoppingCartItemResponseDTO>> getItemsForUser(@PageableDefault(size = 10) Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDERITEM.getFields());
        Page<ShoppingCartItem> items = shoppingCartItemService.findItemsByUser(userId, pageable);
        return ResponseEntity.ok(items.map(ShoppingCartItemMapper::toDTO));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ShoppingCartItemResponseDTO> getItemsForUser(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ShoppingCartItem item = shoppingCartItemService.findItemByUserAndProduct(productId, userId);
        return ResponseEntity.ok(ShoppingCartItemMapper.toDTO(item));
    }
}
