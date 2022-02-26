package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.businessLogic.ProductBusinessLogic;
import com.javabootcamp.shoppingflow.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    ProductBusinessLogic productBusinessLogic;

    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getProducts(@RequestParam Optional<String> name) {
        return productBusinessLogic.getProductsByQueryParams(name);
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public Product getProductById(@PathVariable Optional<Integer> id) {
        return productBusinessLogic.getProductById(id);
    }

}