package com.javabootcamp.shoppingflow.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String img;
    private Double price;
    private Double discount;
    @Nullable
    private Double rating;
    private Double fullPrice;
    private Integer warranty;
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;
}
