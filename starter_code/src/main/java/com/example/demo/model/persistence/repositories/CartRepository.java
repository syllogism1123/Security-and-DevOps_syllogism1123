package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByCustomer(Customer customer);
}
