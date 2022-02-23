package com.javabootcamp.shoppingflow.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;
}
