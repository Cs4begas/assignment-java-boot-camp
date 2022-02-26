package com.javabootcamp.shoppingflow.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBasketRequest {
    private Integer productId;
    private String size;
}
