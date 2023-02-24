package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.Customer;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<Customer, Long> {
	Customer findByUsername(String username);
}
