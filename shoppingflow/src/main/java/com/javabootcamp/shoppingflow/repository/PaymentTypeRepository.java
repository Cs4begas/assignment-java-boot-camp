package com.javabootcamp.shoppingflow.repository;

import com.javabootcamp.shoppingflow.model.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {
}