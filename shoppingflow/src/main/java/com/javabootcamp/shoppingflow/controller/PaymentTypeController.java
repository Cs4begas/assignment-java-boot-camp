package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.model.entity.PaymentType;
import com.javabootcamp.shoppingflow.repository.PaymentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentTypeController {
    @Autowired
    private PaymentTypeRepository paymentTypeRepository;


    @GetMapping("/api/payment-types")
    @ResponseBody
    public List<PaymentType> getPaymentTypes() {
        return paymentTypeRepository.findAll();
    }
}
