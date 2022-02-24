package com.javabootcamp.shoppingflow.repository;

import com.javabootcamp.shoppingflow.model.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
}