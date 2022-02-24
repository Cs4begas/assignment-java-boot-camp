package com.javabootcamp.shoppingflow.businessLogic;

import com.javabootcamp.shoppingflow.exception.NotFoundException;
import com.javabootcamp.shoppingflow.exception.ValidationException;
import com.javabootcamp.shoppingflow.model.entity.*;
import com.javabootcamp.shoppingflow.model.enums.OrderStatusType;
import com.javabootcamp.shoppingflow.model.request.CreateBasketRequest;
import com.javabootcamp.shoppingflow.repository.BasketRepository;
import com.javabootcamp.shoppingflow.repository.CustomerRepository;
import com.javabootcamp.shoppingflow.repository.OrderStatusRepository;
import com.javabootcamp.shoppingflow.repository.ProductRepository;
import com.javabootcamp.shoppingflow.validation.BasketValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BasketBusinessLogic {

    private BasketRepository basketRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private OrderStatusRepository orderStatusRepository;
    private BasketValidation basketValidation;

    @Autowired
    public BasketBusinessLogic(BasketRepository basketRepository, CustomerRepository customerRepository, ProductRepository productRepository, OrderStatusRepository orderStatusRepository, BasketValidation basketValidation) {
        this.basketRepository = basketRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderStatusRepository = orderStatusRepository;
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
        return customerRepository.findById(customerIdValue).orElseThrow(() -> new NotFoundException(String.format("Not found customerId %d", customerIdValue)));
    }

    private Product GetProduct(int productId) {
        return productRepository.findById(productId).orElseThrow(() -> new NotFoundException(String.format("Not found productId %s", productId)));
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

    public Basket HandleBasketOrder(Optional<Integer> customerId, Optional<Integer> basketId, OrderStatusType orderStatusType) {
        basketValidation.ValidateGetBasketRequest(customerId, basketId);
        int customerIdValue = customerId.get();
        int basketIdValue = basketId.get();
        customerRepository.findById(customerId.get()).orElseThrow(() -> new NotFoundException(String.format("Not found customerId %d", customerIdValue)));
        Basket basket = basketRepository.findByIdAndCustomerId(customerIdValue, basketIdValue).orElseThrow(() -> new NotFoundException("Customer's basket is not found"));
        OrderStatus orderStatus = orderStatusRepository.findById(orderStatusType.getId()).get();
        switch (orderStatusType) {
            case CHECKOUT:
                basket = CreateCheckoutBasketOrder(basket, orderStatus);
                break;
        }
        basketRepository.save(basket);
        return basket;
    }

    private Basket CreateCheckoutBasketOrder(Basket basket, OrderStatus orderStatus) {
        if (basket.getBasketOrder() != null) {
            throw new ValidationException("Basket is already checkout");
        }
        BasketOrder basketOrder = new BasketOrder();
        basketOrder.setBasket(basket);
        basketOrder.setOrderStatus(orderStatus);
        basketOrder.setOrderAmount(basket.getBasketItem().getItemNetPrice());
        basket.setBasketOrder(basketOrder);
        return basket;
    }
}
