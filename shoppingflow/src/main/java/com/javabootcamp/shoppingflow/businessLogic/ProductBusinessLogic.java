package com.javabootcamp.shoppingflow.businessLogic;

import com.javabootcamp.shoppingflow.exception.NotFoundException;
import com.javabootcamp.shoppingflow.model.entity.Product;
import com.javabootcamp.shoppingflow.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductBusinessLogic {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByQueryParams(Optional<String> name) {
        if (name.isPresent()) {
            List<Product> products = productRepository.findByNameIgnoreCaseContaining(name.get());
            if (products.size() == 0) {
                throw new NotFoundException(String.format("Not found product name like %s", name.get()));
            }
            return products;
        }
        return productRepository.findAll();
    }
}
