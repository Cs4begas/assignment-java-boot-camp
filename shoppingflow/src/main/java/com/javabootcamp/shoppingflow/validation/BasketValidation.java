package com.javabootcamp.shoppingflow.validation;

import com.javabootcamp.shoppingflow.exception.NotFoundException;
import com.javabootcamp.shoppingflow.exception.ValidationException;
import com.javabootcamp.shoppingflow.model.entity.Basket;
import com.javabootcamp.shoppingflow.model.entity.request.CreateBasketRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class BasketValidation {

    public void ValidateCreateBasketRequest(Optional<Integer> customerId, CreateBasketRequest createBasketRequest) {
        ValidateCustomerIdRequest(customerId);
        if (isNull(createBasketRequest)) {
            throw new ValidationException("Body CreateBasketRequest is null");
        }
        if (isNull(createBasketRequest.getProductId())) {
            throw new ValidationException("Body CreateBasketRequest productId is null");
        }
        if (isNull(createBasketRequest.getSize())) {
            throw new ValidationException("Body CreateBasketRequest size is null");
        }
    }

    private void ValidateCustomerIdRequest(Optional<Integer> customerId) {
        if (!customerId.isPresent()) {
            throw new ValidationException("Please insert customerId on header");
        }
    }

    public void ValidateGetBasketRequest(Optional<Integer> customerId, Optional<Integer> basketId) {
        ValidateCustomerIdRequest(customerId);
        ValidateBasketIdRequest(basketId);
    }

    private void ValidateBasketIdRequest(Optional<Integer> basketId) {
        if (!basketId.isPresent()) {
            throw new ValidationException("Please insert id value in path parameters");
        }
    }

    public void ValidateExistBasket(Basket basket, int basketId) {
        if (basket == null) {
            throw new NotFoundException(String.format("Not found basket Id %d", basketId));
        }
    }
}
