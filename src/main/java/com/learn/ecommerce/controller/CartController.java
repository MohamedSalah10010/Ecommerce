package com.learn.ecommerce.controller;


import com.learn.ecommerce.DTO.Cart.CartDTO;
import com.learn.ecommerce.services.CartService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Getter
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/get")
    public ResponseEntity<CartDTO> getCart() {


    }
}
