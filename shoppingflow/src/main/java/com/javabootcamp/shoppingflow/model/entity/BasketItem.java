package com.javabootcamp.shoppingflow.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String itemName;
    private Double itemPrice;
    private String itemSize;
    private Double itemDiscount;
    private Double itemNetPrice;

    @OneToOne()
    @JoinColumn(name = "basket_id", referencedColumnName = "id")
    @JsonBackReference
    private Basket basket;

    @OneToOne()
    @JoinColumn(name = "product_id")
    private Product product;
    private Date createdAt = new Date();
}
