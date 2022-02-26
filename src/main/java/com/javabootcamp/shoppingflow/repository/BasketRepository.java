package com.javabootcamp.shoppingflow.repository;

import com.javabootcamp.shoppingflow.model.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
    Optional<Basket> findByIdAndCustomerId(int customerId, int basketId);
}