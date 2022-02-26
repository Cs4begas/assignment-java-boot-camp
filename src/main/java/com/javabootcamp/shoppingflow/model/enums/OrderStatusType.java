package com.javabootcamp.shoppingflow.model.enums;

public enum OrderStatusType {
    CHECKOUT(1,"Checkout"),CONFIRM_SHIPPING(2,"Confirm-Shipping"),CONFIRM_ORDER(3,"Confirm-Order");
    private Integer id;
    private String name;
    OrderStatusType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
