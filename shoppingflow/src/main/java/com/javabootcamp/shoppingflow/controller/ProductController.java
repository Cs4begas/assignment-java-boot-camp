package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.model.entity.Product;
import com.javabootcamp.shoppingflow.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

}