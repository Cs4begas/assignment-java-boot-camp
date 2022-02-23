package com.javabootcamp.shoppingflow.repository;

import com.javabootcamp.shoppingflow.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}