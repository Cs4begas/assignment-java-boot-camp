package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.businessLogic.BasketBusinessLogic;
import com.javabootcamp.shoppingflow.model.entity.Basket;
import com.javabootcamp.shoppingflow.model.entity.request.CreateBasketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BasketController {
    @Autowired
    private BasketBusinessLogic basketBusinessLogic;


    @PostMapping("/api/baskets")
    @ResponseBody
    public Basket CreateBasket(@RequestHeader("customerId") Optional<Integer> customerId, @RequestBody @Validated CreateBasketRequest basketRequest) {
        return basketBusinessLogic.CreateBasket(customerId, basketRequest);
    }
}
