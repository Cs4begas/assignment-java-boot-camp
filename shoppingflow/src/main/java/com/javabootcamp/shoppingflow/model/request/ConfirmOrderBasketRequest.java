package com.javabootcamp.shoppingflow.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOrderBasketRequest {
    private Integer cardNumber;
    private String cardOwnerName;
    private Integer cardExpiredMonth;
    private Integer cardExpiredYear;
    private Integer cardCcvCvv;
    private Integer paymentTypeId;
}
