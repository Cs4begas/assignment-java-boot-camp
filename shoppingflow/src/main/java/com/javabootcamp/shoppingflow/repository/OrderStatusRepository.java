package com.javabootcamp.shoppingflow.repository;

import com.javabootcamp.shoppingflow.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
}