package com.javabootcamp.shoppingflow.businessLogic;

import com.javabootcamp.shoppingflow.exception.NotFoundException;
import com.javabootcamp.shoppingflow.exception.ValidationException;
import com.javabootcamp.shoppingflow.model.entity.*;
import com.javabootcamp.shoppingflow.model.enums.OrderStatusType;
import com.javabootcamp.shoppingflow.model.request.ConfirmOrderBasketRequest;
import com.javabootcamp.shoppingflow.model.request.CreateBasketRequest;
import com.javabootcamp.shoppingflow.model.response.BasketSummaryResponse;
import com.javabootcamp.shoppingflow.repository.*;
import com.javabootcamp.shoppingflow.validation.BasketValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class BasketBusinessLogic {

    private BasketRepository basketRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private OrderStatusRepository orderStatusRepository;
    private PaymentTypeRepository paymentTypeRepository;
    private BasketValidation basketValidation;

    @Autowired
    public BasketBusinessLogic(BasketRepository basketRepository, CustomerRepository customerRepository, ProductRepository productRepository, OrderStatusRepository orderStatusRepository, PaymentTypeRepository paymentTypeRepository, BasketValidation basketValidation) {
        this.basketRepository = basketRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.paymentTypeRepository = paymentTypeRepository;
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
        Basket basket = basketRepository.findByIdAndCustomerId(customerIdValue, basketIdValue).orElseThrow(() -> new NotFoundException("Customer's basket is not found"));;
        return basket;
    }

    public Basket HandleBasketOrder(Optional<Integer> customerId, Optional<Integer> basketId, OrderStatusType orderStatusType, ConfirmOrderBasketRequest confirmOrderBasketRequest) {
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
            case CONFIRM_SHIPPING:
                basket = ConfirmShippingBasketOrder(basket, orderStatus);
                break;
            case CONFIRM_ORDER:
                basket = ConfirmOrderBasket(basket, orderStatus, confirmOrderBasketRequest);
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

    public Basket ConfirmShippingBasketOrder(Basket basket, OrderStatus orderStatus) {
        String orderStatusCheckout = OrderStatusType.CHECKOUT.getName();
        if (!basket.getBasketOrder().getOrderStatus().getDescription().equals(orderStatusCheckout)) {
            throw new ValidationException("Basket status is not Checkout");
        }
        BasketOrder basketOrder = basket.getBasketOrder();
        basketOrder.setOrderStatus(orderStatus);
        return basket;
    }

    public Basket ConfirmOrderBasket(Basket basket, OrderStatus orderStatus, ConfirmOrderBasketRequest confirmOrderBasketRequest) {
        String orderStatusConfirmShipping = OrderStatusType.CONFIRM_SHIPPING.getName();
        if (!basket.getBasketOrder().getOrderStatus().getDescription().equals(orderStatusConfirmShipping)) {
            throw new ValidationException("Basket status is not Confirm-Shipping");
        }
        BasketOrder basketOrder = basket.getBasketOrder();
        int paymentTypeId = confirmOrderBasketRequest.getPaymentTypeId();
        PaymentType paymentType = paymentTypeRepository.findById(paymentTypeId).orElseThrow(() -> new NotFoundException(String.format("Not found PaymentTypeId %d", paymentTypeId)));

        BasketPayment payment = new BasketPayment();
        payment.setCardNumber(confirmOrderBasketRequest.getCardNumber());
        payment.setCardOwnerName(confirmOrderBasketRequest.getCardOwnerName());
        payment.setCardExpiredMonth(confirmOrderBasketRequest.getCardExpiredMonth());
        payment.setCardExpiredYear(confirmOrderBasketRequest.getCardExpiredYear());
        payment.setCardCcvCvv(confirmOrderBasketRequest.getCardCcvCvv());
        payment.setPaymentType(paymentType);

        basketOrder.setOrderStatus(orderStatus);
        basketOrder.setBasketPayment(payment);
        basketOrder.setInvoiceNumber(GenerateInvoiceNumber());

        return basket;

    }

    public String GenerateInvoiceNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    public BasketSummaryResponse GetBasketSummary(Optional<Integer> customerId, Optional<Integer> basketId) {
        basketValidation.ValidateGetBasketRequest(customerId, basketId);
        int customerIdValue = customerId.get();
        int basketData = basketId.get();

        Basket basket = basketRepository.findByIdAndCustomerId(customerIdValue, basketData).orElseThrow(() -> new NotFoundException("Customer's basket is not found"));;
        if(basket.getBasketOrder() == null){
            throw new ValidationException("Basket status is not Checkout");
        }
        if(!basket.getBasketOrder().getOrderStatus().getDescription().equals(OrderStatusType.CONFIRM_ORDER.getName())){
            throw new ValidationException("Basket status is not Confirm-Order");
        }
        BasketSummaryResponse basketSummaryResponse = MappingBasketSummary(basket);

        return basketSummaryResponse;
    }

    private BasketSummaryResponse MappingBasketSummary(Basket basket) {
        BasketOrder basketOrder = basket.getBasketOrder();
        BasketPayment basketPayment = basketOrder.getBasketPayment();

        BasketSummaryResponse basketSummaryResponse = new BasketSummaryResponse();
        basketSummaryResponse.setInvoiceNumber(basketOrder.getInvoiceNumber());
        basketSummaryResponse.setPaymentType(basketPayment.getPaymentType());
        basketSummaryResponse.setTransactionDate(basketPayment.getCreatedAt());
        basketSummaryResponse.setNetAmount(basketOrder.getOrderAmount());
        basketSummaryResponse.setPayer(basketPayment.getCardOwnerName());

        return basketSummaryResponse;
    }
}
