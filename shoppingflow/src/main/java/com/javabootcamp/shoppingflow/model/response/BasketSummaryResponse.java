package com.javabootcamp.shoppingflow.model.response;

import com.javabootcamp.shoppingflow.model.entity.PaymentType;
import lombok.Data;

import java.util.Date;

@Data
public class BasketSummaryResponse {
    private String invoiceNumber;
    private String payer;
    private Date transactionDate;
    private PaymentType paymentType;
    private Double netAmount;
}
