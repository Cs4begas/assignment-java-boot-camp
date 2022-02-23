package com.javabootcamp.shoppingflow.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private Date createdAt = new Date();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "basket")
    @JsonManagedReference
    private BasketOrder basketOrder;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "basket")
    @JsonManagedReference
    private BasketItem basketItem;
}