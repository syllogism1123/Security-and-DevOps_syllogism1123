package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Customer;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public CartController(UserRepository userRepository, CartRepository cartRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
        Customer user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            log.error("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            log.error("Item not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);
        log.info("Successfully add to cart");
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
        Customer user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            log.error("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            log.error("Item not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        if (request.getQuantity() > cart.getItems().size()) {
            log.error("Invalid quantity");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);
        log.info("Successfully remove from cart");
        return ResponseEntity.ok(cart);
    }

}
