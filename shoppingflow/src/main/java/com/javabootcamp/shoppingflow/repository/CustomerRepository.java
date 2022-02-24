package com.javabootcamp.shoppingflow.repository;

import com.javabootcamp.shoppingflow.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findById (Integer id);
}