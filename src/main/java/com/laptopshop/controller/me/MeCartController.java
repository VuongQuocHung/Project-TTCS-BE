package com.laptopshop.controller.me;

import com.laptopshop.dto.CartDTO;
import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Quản lý giỏ hàng của tôi")
public class MeCartController {

    private final CartService cartService;

    @GetMapping
    public CartDTO getCart() {
        return cartService.getCart(SecurityUtils.getCurrentUserId());
    }

    @PostMapping
    public CartDTO addToCart(@RequestParam Long variantId, @RequestParam(defaultValue = "1") Integer quantity) {
        return cartService.addToCart(SecurityUtils.getCurrentUserId(), variantId, quantity);
    }

    @PutMapping("/items/{id}")
    public CartDTO updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        return cartService.updateQuantity(SecurityUtils.getCurrentUserId(), id, quantity);
    }

    @DeleteMapping("/items/{id}")
    public CartDTO removeFromCart(@PathVariable Long id) {
        return cartService.removeFromCart(SecurityUtils.getCurrentUserId(), id);
    }

    @DeleteMapping
    public void clearCart() {
        cartService.clearCart(SecurityUtils.getCurrentUserId());
    }
}
