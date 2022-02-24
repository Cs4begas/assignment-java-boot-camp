package com.javabootcamp.shoppingflow.businessLogic;

import com.javabootcamp.shoppingflow.exception.NotFoundException;
import com.javabootcamp.shoppingflow.model.entity.*;
import com.javabootcamp.shoppingflow.model.entity.request.CreateBasketRequest;
import com.javabootcamp.shoppingflow.repository.BasketRepository;
import com.javabootcamp.shoppingflow.repository.CustomerRepository;
import com.javabootcamp.shoppingflow.repository.ProductRepository;
import com.javabootcamp.shoppingflow.validation.BasketValidation;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BasketBusinessLogic {

    private BasketRepository basketRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private BasketValidation basketValidation;

    @Autowired
    public BasketBusinessLogic(BasketRepository basketRepository, CustomerRepository customerRepository, ProductRepository productRepository, BasketValidation basketValidation) {
        this.basketRepository = basketRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.basketValidation = basketValidation;
    }

    public Basket CreateBasket(Optional<Integer> customerId, CreateBasketRequest basketRequest) {
        basketValidation.ValidateCreateBasketRequest(customerId, basketRequest);
        int customerIdValue = customerId.get();
        int productId = basketRequest.getProductId();

        Customer customer = GetCustomer(customerIdValue);
        Product product = GetProduct(productId);
        List<ProductSize> productSize = product.getProductType().getProductSizes();
        String selectedSize = GetSelectedSize(basketRequest, productId, productSize);

        Basket basket = CreateBasketToSave(customer, product, selectedSize);
        basketRepository.save(basket);

        return basket;
    }

    private String GetSelectedSize(CreateBasketRequest basketRequest, int productId, List<ProductSize> productSizes) {
        ProductSize productSize = productSizes.stream().filter(x -> {
            return x.getDescription().equals(basketRequest.getSize());
        }).findFirst().orElseThrow(() -> new NotFoundException(String.format("Not found input size %s of productId %s", basketRequest.getSize(), productId)));
        return productSize.getDescription();
    }

    private Customer GetCustomer(int customerIdValue) {
        Customer customer = customerRepository.findById(customerIdValue).orElseThrow(() -> new NotFoundException(String.format("Not found customerId %d", customerIdValue)));
        return customer;
    }

    private Product GetProduct(int productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException(String.format("Not found productId %s", productId)));
        return product;
    }

    private Basket CreateBasketToSave(Customer customer, Product product, String selectedSize) {
        BasketItem basketItem = new BasketItem();
        basketItem.setProduct(product);
        basketItem.setItemName(product.getName());
        basketItem.setItemDiscount(product.getDiscount());
        basketItem.setItemSize(selectedSize);
        basketItem.setItemPrice(product.getFullPrice());
        basketItem.setItemNetPrice(product.getPrice());

        Basket basket = new Basket();
        basket.setCustomer(customer);
        basketItem.setBasket(basket);
        basket.setBasketItem(basketItem);

        return basket;
    }

    public Basket GetBasket(Optional<Integer> customerId, Optional<Integer> basketId) {
        basketValidation.ValidateGetBasketRequest(customerId, basketId);
        int customerIdValue = customerId.get();
        int basketIdValue = basketId.get();
        customerRepository.findById(customerIdValue).orElseThrow(() -> new NotFoundException(String.format("Not found customerId %d", customerId.get())));
        Optional<Basket> basketData = basketRepository.findByIdAndCustomerId(customerIdValue, basketIdValue);
        Basket basket = basketData.get();
        basketValidation.ValidateExistBasket(basket, basketId.get());
        return basket;
    }

}
