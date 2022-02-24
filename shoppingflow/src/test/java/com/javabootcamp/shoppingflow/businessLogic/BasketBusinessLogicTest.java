package com.javabootcamp.shoppingflow.businessLogic;

import com.javabootcamp.shoppingflow.exception.NotFoundException;
import com.javabootcamp.shoppingflow.exception.ValidationException;
import com.javabootcamp.shoppingflow.model.entity.*;
import com.javabootcamp.shoppingflow.model.entity.request.CreateBasketRequest;
import com.javabootcamp.shoppingflow.repository.BasketRepository;
import com.javabootcamp.shoppingflow.repository.CustomerRepository;
import com.javabootcamp.shoppingflow.repository.ProductRepository;
import com.javabootcamp.shoppingflow.validation.BasketValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketBusinessLogicTest {
    @InjectMocks
    private BasketValidation basketValidation;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BasketRepository basketRepository;

    private BasketBusinessLogic basketBusinessLogic;

    private Customer customer;

    private Product product;

    private Basket basket;

    private BasketItem basketItem;

    @BeforeEach
    public void setUp() {
        basketBusinessLogic = new BasketBusinessLogic(basketRepository, customerRepository, productRepository, basketValidation);
        customer = CreateCustomer();
        product = CreateProduct();
        basketItem = CreateBasketItem();
        basket = CreateBasket();
    }

    private Customer CreateCustomer() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("Messi");
        return customer;
    }

    private Product CreateProduct() {
        Product product = new Product();
        product.setId(1);
        product.setName("Adidas NMD");
        product.setDiscount(0.28);
        product.setPrice(10000.0);

        ProductSize productSize = new ProductSize();
        productSize.setId(1);
        productSize.setDescription("34");
        ProductType productType = new ProductType();
        productType.setId(1);
        productType.setProductSizes(List.of(productSize));

        product.setProductType(productType);
        return product;
    }

    private BasketItem CreateBasketItem() {
        BasketItem basketItem = new BasketItem();
        basketItem.setProduct(product);
        basketItem.setItemNetPrice(8000.0);
        return basketItem;
    }

    private Basket CreateBasket() {
        Basket basket = new Basket();
        basket.setId(1);
        basket.setCustomer(customer);
        basket.setBasketItem(basketItem);
        return basket;
    }


    @Test
    @DisplayName("Case Validate Test Method CreateBasket ด้วย customerId = null และ CreateBasketRequest = null ต้องได้ message Please insert customerId on header")
    void caseCreateBasketWithCustomerIdIsNull() {
        Optional<Integer> customerId = Optional.empty();
        CreateBasketRequest createBasketRequest = null;

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.CreateBasket(customerId, createBasketRequest), "Please insert customerId on header");
        assertEquals("Please insert customerId on header", exception.getMessage());
    }

    @Test
    @DisplayName("Case Validate Test Method CreateBasket ด้วย customerId = 1 และ CreateBasketRequest = null ต้องได้ message Body CreateBasketRequest is null")
    void caseCreateBasketWithCreateBasketRequestIsNull() {
        Optional<Integer> customerId = Optional.of(1);
        CreateBasketRequest createBasketRequest = null;

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.CreateBasket(customerId, createBasketRequest));
        assertEquals("Body CreateBasketRequest is null", exception.getMessage());
    }

    @Test
    @DisplayName("Case Validate Test Method CreateBasket ด้วย customerId = 1 และ CreateBasketRequest.productId = null ต้องได้ message Body CreateBasketRequest productId is null")
    void caseCreateBasketWithCreateBasketRequestProductIdIsNull() {
        Optional<Integer> customerId = Optional.of(1);
        CreateBasketRequest createBasketRequest = new CreateBasketRequest(null, "34");

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.CreateBasket(customerId, createBasketRequest));
        assertEquals("Body CreateBasketRequest productId is null", exception.getMessage());
    }

    @Test
    @DisplayName("Case Validate Test Method CreateBasket ด้วย customerId = 1 และ CreateBasketRequest.size = null ต้องได้ message Body CreateBasketRequest size is null")
    void caseCreateBasketWithCreateBasketRequestSizeIdIsNull() {
        Optional<Integer> customerId = Optional.of(1);
        CreateBasketRequest createBasketRequest = new CreateBasketRequest(1, null);

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.CreateBasket(customerId, createBasketRequest));
        assertEquals("Body CreateBasketRequest size is null", exception.getMessage());
    }
    @Test
    @DisplayName("Case NotFound Test Method CreateBasket ด้วย customerId = 3 และ CreateBasketRequest ต้องได้ Not found customerId 3")
    void caseCreateBasketWithCustomerIdIs3NotFound() {
        Optional<Integer> customerId = Optional.of(3);
        CreateBasketRequest createBasketRequest = new CreateBasketRequest(1, "34");
        createBasketRequest.setProductId(1);

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.CreateBasket(customerId, createBasketRequest), "Not found customerId 3");
        assertEquals("Not found customerId 3", exception.getMessage());
    }

    @Test
    @DisplayName("Case NotFound Test Method CreateBasket ด้วย customerId = 1 และ CreateBasketRequest ด้วย productId 3 ต้องได้ Not found productId 3")
    void caseCreateBasketWithProductIdIs3NotFound() {
        Optional<Integer> customerId = Optional.of(1);
        CreateBasketRequest createBasketRequest = new CreateBasketRequest(3, "34");
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.CreateBasket(customerId, createBasketRequest), "Not found productId 3");
        assertEquals("Not found productId 3", exception.getMessage());
    }

    @Test
    @DisplayName("Case Success Test Method CreateBasket ด้วย customerId = 1 และ CreateBasketRequest ต้องได้ ฺBasket ไม่ null , Basket.Customer ไม่ null และ Basket.BasketProduct ไม่ null")
    void caseCreateBasketSuccess() {
        Optional<Integer> customerId = Optional.of(1);
        CreateBasketRequest createBasketRequest = new CreateBasketRequest(1, "34");
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        Basket basketResult = basketBusinessLogic.CreateBasket(customerId, createBasketRequest);

        assertNotNull(basketResult);
        assertNotNull(basketResult.getCustomer());
        assertNotNull(basketResult.getBasketItem());
    }

    @Test
    @DisplayName("Case Validate Test Method GetBasket ด้วย customerId = null และ basketId = null ต้องได้ message Please insert customerId on header")
    void caseGetBasketWithCustomerIdIsNull() {
        Optional<Integer> customerId = Optional.ofNullable(null);
        Optional<Integer> basketId = Optional.ofNullable(null);

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.GetBasket(customerId, basketId));
        assertEquals(exception.getMessage(),"Please insert customerId on header");
    }

    @Test
    @DisplayName("Case Validate Test Method GetBasket ด้วย customerId = 1 และ basketId = null ต้องได้ message Please insert id value in path parameters")
    void caseGetBasketWithBasketIdIsNull() {
        Optional<Integer> customerId = Optional.ofNullable(1);
        Optional<Integer> basketId = Optional.ofNullable(null);

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.GetBasket(customerId, basketId));
        assertEquals(exception.getMessage(),"Please insert id value in path parameters");
    }

    @Test
    @DisplayName("Case NotFound Test Method GetBasket ด้วย customerId = 3 และ basketId = 1 ต้องได้ message Not found customerId 3")
    void caseGetBasketWithCustomerIdIs3() {
        Optional<Integer> customerId = Optional.ofNullable(3);
        Optional<Integer> basketId = Optional.ofNullable(1);

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.GetBasket(customerId, basketId));
        assertEquals(exception.getMessage(),"Not found customerId 3");
    }

    @Test
    @DisplayName("Case Success Test Method GetBasket ด้วย customerId = 1 และ basketId 1 ต้องได้ ฺBasket ไม่ null , Basket.Customer ไม่ null และ Basket.BasketProduct ไม่ null")
    void caseGetBasketSuccess() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basket));
        Basket basketResult = basketBusinessLogic.GetBasket(customerId, basketId);

        assertNotNull(basketResult);
        assertNotNull(basketResult.getCustomer());
        assertNotNull(basketResult.getBasketItem());
    }
}