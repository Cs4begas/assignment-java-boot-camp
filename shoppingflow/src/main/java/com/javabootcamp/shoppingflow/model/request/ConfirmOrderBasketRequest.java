package com.javabootcamp.shoppingflow.model.request;

import lombok.Data;

@Data
public class ConfirmOrderBasketRequest {
    private Integer cardNumber;
    private String cardOwnerName;
    private Integer cardExpiredMonth;
    private Integer cardExpiredYear;
    private Integer cardCcvCvv;
    private Integer paymentTypeId;
}
