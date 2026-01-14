package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.Cart.CartDTO;
import com.learn.ecommerce.DTO.Cart.CartStatusDTO;
import com.learn.ecommerce.DTO.Cart.UpdateQuantityDTO;
import com.learn.ecommerce.DTO.CartItem.AddItemDTO;
import com.learn.ecommerce.DTO.CartItem.ItemDTO;
import com.learn.ecommerce.entity.*;
import com.learn.ecommerce.exceptionhandler.CartIsEmptyException;
import com.learn.ecommerce.exceptionhandler.InsufficientStockException;
import com.learn.ecommerce.exceptionhandler.ItemNotFoundException;
import com.learn.ecommerce.exceptionhandler.ProductNotFoundException;
import com.learn.ecommerce.repository.CartItemRepo;
import com.learn.ecommerce.repository.CartRepo;
import com.learn.ecommerce.repository.InventoryRepo;
import com.learn.ecommerce.repository.ProductRepo;
import com.learn.ecommerce.utils.CartStatus;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Getter
@Service
public class CartService {
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;

    private final ProductRepo productRepo;
    private final InventoryRepo inventoryRepo;

    public CartService(CartRepo cartRepo, CartItemRepo cartItemRepo, ProductRepo productRepo, InventoryRepo inventoryRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
    }

    @Transactional
    public CartDTO getCurrentActiveCart(LocalUser user) {
        Optional<Cart> cartOptional = cartRepo.findByUserIdAndStatus(user.getId(),CartStatus.ACTIVE);
        if (cartOptional.isPresent()) {
            if (cartOptional.get().isDeleted()) {
                Cart newCart = createNewCart(user);
                return new CartDTO().builder()
                        .id(newCart.getId())
                        .status(newCart.getStatus())
                        .totalPrice(0)
                        .build();
            }
            Cart cart = cartOptional.get();
            List<ItemDTO> itemListDTO = mappingListItemDTO(cart);


            double totalPrice = 0;
            for (ItemDTO item : itemListDTO) {
                totalPrice += item.getTotalPrice();
            }

            return new CartDTO().builder()
                    .id(cart.getId())
                    .status(cart.getStatus())
                    .items(itemListDTO)
                    .totalPrice(totalPrice)
                    .build();
        }


        Cart newCart = createNewCart(user);
        return new CartDTO().builder()
                .id(newCart.getId())
                .status(newCart.getStatus())
                .totalPrice(0)
                .build();

    }

    private Cart createNewCart(LocalUser user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setStatus(CartStatus.ACTIVE);
        newCart.setDeleted(false);
        cartRepo.save(newCart);

        return newCart;
    }

    private List<ItemDTO> mappingListItemDTO(Cart cart) {
        List<ItemDTO> itemListDTO = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            if (item.isDeleted()) continue;
            itemListDTO.add(
                    new ItemDTO().builder()
                            .id(item.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getPriceAtAddition())
                            .totalPrice(item.getPriceAtAddition() * item.getQuantity())
                            .build()
            );
        }
        return itemListDTO;
    }

    @Transactional
    public CartItem createNewCartItem(Product product, int quantity, Cart cart) {

           return new CartItem().builder()
                            .product(product)
                            .quantity(quantity)
                            .priceAtAddition(product.getPrice())
                            .cart(cart)
                            .build();

    }


    @Transactional
    public CartStatusDTO addItemToCart(LocalUser user, AddItemDTO dto) {

        Cart cart = cartRepo
                .findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseGet(() -> createNewCart(user));

        Product product = productRepo.findById(dto.getProductId())
                .filter(p -> !p.isDeleted())
                .orElseThrow(ProductNotFoundException::new);

        CartItem item = cartItemRepo
                .findByCartAndProduct(cart, product)
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .priceAtAddition(product.getPrice())
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + dto.getQuantity());

        cartItemRepo.save(item);

        return CartStatusDTO.builder()
                .id(cart.getId())
                .items(mappingListItemDTO(cart))
                .statusMessage("Item added successfully")
                .build();
    }
/*
    //    public CartStatusDTO addItemToCart(LocalUser user, AddItemDTO item) {
//
//        Optional<Cart> cartOptional = cartRepo.findByUserId(user.getId());
//        if (cartOptional.get().getStatus() == CartStatus.CHECKED_OUT) {
//            throw new IllegalStateException("Cannot modify a checked-out cart");
//        }
//        if (!cartOptional.isPresent() || cartOptional.get().isDeleted()) {
//            Cart cart = createNewCart(user);
//            CartItem cartItem = createNewCartItem(item.getProductId(), item.getQuantity(), cart);
//            cart.getItems().add(cartItem);
//            cartItemRepo.save(cartItem);
//
//            List<ItemDTO> itemListDTO = mappingListItemDTO(cart);
//            return new CartStatusDTO().builder()
//                    .id(cart.getId())
//                    .items(itemListDTO)
//                    .statusMessage("Item been added successfully")
//                    .build();
//        }
//
//        Cart cart = cartOptional.get();
//        Product product = productRepo.findById(item.getProductId()).orElseThrow(ProductNotFoundException::new);
//        CartItem cartItem = new CartItem();
//        Optional<CartItem> existingItem =
//                cartItemRepo.findByCartAndProduct(cart, product);
//        if (existingItem.isPresent()) {
//            cartItem = existingItem.get();
//            cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
//        } else {
//            cartItem = createNewCartItem(item.getProductId(), item.getQuantity(), cart);
//            cart.getItems().add(cartItem);
//
//        }
//        cartItemRepo.save(cartItem);
//        List<ItemDTO> itemListDTO = mappingListItemDTO(cart);
//        return new CartStatusDTO().builder()
//                .id(cart.getId())
//                .items(itemListDTO)
//                .statusMessage("Item been added successfully")
//                .build();
//    }
  */

    @Transactional
    public CartDTO checkoutCart(Cart cart) {

        if(cart.getStatus()!= CartStatus.ACTIVE) {
            throw new IllegalStateException("Cart status is not ACTIVE, can't checkout cart");
        }
        if(cart.getItems().isEmpty()) {
            throw new CartIsEmptyException("Cart items is empty, can't checkout cart");
        }

        for (CartItem item : cart.getItems()) {
            Inventory inventory = item.getProduct().getInventory();

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException();
            }

            inventory.setQuantity(
                    inventory.getQuantity() - item.getQuantity()
            );
            inventoryRepo.save(inventory);

        }

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepo.save(cart);
        List<ItemDTO> itemListDTO = mappingListItemDTO(cart);


        double totalPrice = 0;
        for (ItemDTO item : itemListDTO) {
            totalPrice += item.getTotalPrice();
        }

        return new CartDTO().builder()
                .id(cart.getId())
                .status(cart.getStatus())
                .items(itemListDTO)
                .totalPrice(totalPrice)
                .build();

    }

    @Transactional
    public CartStatusDTO updateCartItemQuantity(LocalUser user,Long itemId, UpdateQuantityDTO updateQuantityDTO) {


       Optional<CartItem> cartItemOptional = cartItemRepo.findById(itemId);
       if(!cartItemOptional.isPresent()) {
           throw new ItemNotFoundException("item isn't found ");
       }
       CartItem cartItem = cartItemOptional.get();
       if(cartItem.getCart().getUser().getId() != user.getId()) {
           throw new AuthenticationServiceException("this user can't access the cart");
       }
       if(updateQuantityDTO.getQuantity() == 0) { deleteCartItem(user,cartItem.getCart(), itemId); }
       cartItem.setQuantity(updateQuantityDTO.getQuantity());
        cartItemRepo.save(cartItem);
       List<ItemDTO> items= mappingListItemDTO(cartItem.getCart());
        return new CartStatusDTO().builder().statusMessage("Item updated successfully").items(items).build();

    }




    @Transactional
    public CartStatusDTO deleteCartItem(LocalUser user,Cart cart,Long cartItemId) {

        Optional<CartItem> cartItemOptional =cartItemRepo.findById(cartItemId);
        if(cartItemOptional.isPresent()) {
            if(cartItemOptional.get().getCart().getUser().getId() != user.getId()) {
                throw new AuthenticationServiceException("this user can't access the cart");
            }
            CartItem cartItem = cartItemOptional.get();
            cartItem.setDeleted(true);
            cart.getItems().remove(cartItem);
            cartRepo.save(cart);
            cartItemRepo.save(cartItem);
            return new CartStatusDTO().builder().statusMessage("Item deleted successfully").build();

        }
        throw new CartIsEmptyException("Cart item id not found, can't delete cartItem");
    }
}




