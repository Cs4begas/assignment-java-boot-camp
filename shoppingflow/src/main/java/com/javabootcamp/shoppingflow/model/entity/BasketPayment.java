package com.javabootcamp.shoppingflow.model.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class BasketPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Integer cardNumber;
    private String cardOwnerName;
    private Integer cardExpiredMonth;
    private Integer cardExpiredYear;
    private Integer cardCcvCvv;
    private Date createdAt = new Date();
    @OneToOne()
    @JoinColumn(name = "payment_type_id")
    private PaymentType paymentType;
}