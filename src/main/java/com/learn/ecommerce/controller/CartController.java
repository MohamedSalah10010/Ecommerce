package com.learn.ecommerce.controller;


import com.learn.ecommerce.DTO.Cart.CartDTO;
import com.learn.ecommerce.DTO.Cart.CartStatusDTO;
import com.learn.ecommerce.DTO.CartItem.AddItemDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.exceptionhandler.UserNotFoundException;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.CartService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Getter
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final LocalUserRepo localUserRepo;

    public CartController(CartService cartService, LocalUserRepo localUserRepo) {
        this.cartService = cartService;
        this.localUserRepo = localUserRepo;
    }

    @GetMapping("/get")
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal User userDetails) {

        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername()).orElseThrow(()-> new UserNotFoundException());
        return new ResponseEntity<>(cartService.getCurrentActiveCart(user),HttpStatus.OK);
    }

    @PostMapping("/create-cart")
    public ResponseEntity<CartDTO> createCart(@AuthenticationPrincipal User userDetails) {

        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException());
        return new ResponseEntity<>(cartService.createNewCartForUser(user), HttpStatus.CREATED);

    }

    @PostMapping("/add-item")
    public ResponseEntity<CartStatusDTO> addItemToCart(@AuthenticationPrincipal User userDetails, @RequestBody AddItemDTO body) {

        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException());
        return new ResponseEntity<>(cartService.addItemToCart(user,body), HttpStatus.OK);

    }

    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<CartStatusDTO> deleteItemFromCart(@AuthenticationPrincipal User userDetails, @PathVariable Long itemId) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException());
        return new ResponseEntity<>(cartService.deleteCartItem(user,itemId), HttpStatus.OK);
    }
    @DeleteMapping("/delete-cart")
    public ResponseEntity<CartStatusDTO> deleteCart(@AuthenticationPrincipal User userDetails) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException());
        return new ResponseEntity<>(cartService.deleteCart(user), HttpStatus.OK);
    }
}
