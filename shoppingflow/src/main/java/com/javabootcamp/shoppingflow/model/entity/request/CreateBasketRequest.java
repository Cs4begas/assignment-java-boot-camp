package com.javabootcamp.shoppingflow.model.entity.request;

import lombok.Data;

import java.util.Optional;
@Data
public class CreateBasketRequest {
    private Integer cardNumber;
    private String cardOwnerName;
    private Integer cardExpiredMonth;
    private Integer cardExpiredYear;
    private Integer cardCcvCvv;
    private Integer paymentTypeId;
    private Optional<Integer> couponId;
}
