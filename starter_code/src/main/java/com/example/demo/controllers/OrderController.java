package com.example.demo.controllers;

import com.example.demo.model.persistence.Customer;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public OrderController(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        Customer user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User ist not found");
            return ResponseEntity.notFound().build();
        }
        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);
        log.info("User submit oder successfully");
        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        Customer user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User ist not found");
            return ResponseEntity.notFound().build();
        }
        log.info("Load UserOrderHistory successfully ");
        return ResponseEntity.ok(orderRepository.findByCustomer(user));
    }
}
