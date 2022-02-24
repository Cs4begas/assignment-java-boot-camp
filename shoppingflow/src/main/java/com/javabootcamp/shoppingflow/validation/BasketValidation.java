package com.javabootcamp.shoppingflow.validation;

import com.javabootcamp.shoppingflow.exception.ValidationException;
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


}
