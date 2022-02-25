package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.businessLogic.BasketBusinessLogic;
import com.javabootcamp.shoppingflow.model.entity.Basket;
import com.javabootcamp.shoppingflow.model.enums.OrderStatusType;
import com.javabootcamp.shoppingflow.model.request.ConfirmOrderBasketRequest;
import com.javabootcamp.shoppingflow.model.request.CreateBasketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BasketController {
    @Autowired
    private BasketBusinessLogic basketBusinessLogic;

    @PostMapping("/api/baskets")
    @ResponseBody
    public Basket CreateBasket(@RequestHeader("customerId") Optional<Integer> customerId, @RequestBody(required = false) CreateBasketRequest basketRequest) {
        return basketBusinessLogic.CreateBasket(customerId, basketRequest);
    }

    @GetMapping("/api/baskets/{id}")
    @ResponseBody
    public Basket GetBasket(@RequestHeader("customerId") Optional<Integer> customerId, @PathVariable Optional<Integer> id) {
        return basketBusinessLogic.GetBasket(customerId, id);
    }

    @PatchMapping("/api/baskets/{id}/checkout")
    @ResponseBody
    public Basket CheckoutBasket(@RequestHeader("customerId") Optional<Integer> customerId, @PathVariable Optional<Integer> id) {
        return basketBusinessLogic.HandleBasketOrder(customerId, id, OrderStatusType.CHECKOUT, null);
    }

    @PatchMapping("/api/baskets/{id}/confirm-shipping")
    @ResponseBody
    public Basket ConfirmShippingBasket(@RequestHeader("customerId") Optional<Integer> customerId, @PathVariable Optional<Integer> id) {
        return basketBusinessLogic.HandleBasketOrder(customerId, id, OrderStatusType.CONFIRM_SHIPPING, null);
    }

    @PatchMapping("/api/baskets/{id}/confirm-order")
    @ResponseBody
    public Basket ConfirmOrderBasket(@RequestHeader("customerId") Optional<Integer> customerId, @PathVariable Optional<Integer> id, @RequestBody ConfirmOrderBasketRequest confirmOrderBasketRequest) {
        return basketBusinessLogic.HandleBasketOrder(customerId, id, OrderStatusType.CONFIRM_ORDER, confirmOrderBasketRequest);
    }

}
