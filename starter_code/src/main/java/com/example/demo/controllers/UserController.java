package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Customer;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserController(UserRepository userRepository,
                          CartRepository cartRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<Customer> findById(@PathVariable Long id) {
        if (!userRepository.findById(id).isPresent()) {
            log.error("User ist not found");
            return ResponseEntity.notFound().build();
        }
        log.info("UserId: " + id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<Customer> findByUserName(@PathVariable String username) {
        Customer user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User ist not found");
            return ResponseEntity.notFound().build();
        }
        log.info("Username: " + user.getUsername(), user.getUsername());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<Customer> createUser(@RequestBody CreateUserRequest createUserRequest) {
        Customer user = new Customer();
        user.setUsername(createUserRequest.getUsername());
        log.info("User set name with " + createUserRequest.getUsername(),
                createUserRequest.getUsername());

        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        if (createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("Password is Invalid");
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        log.debug("Create user successfully  with userName: " + user.getUsername());
        return ResponseEntity.ok(user);
    }

}
