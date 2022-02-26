package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.businessLogic.BasketBusinessLogic;
import com.javabootcamp.shoppingflow.businessLogic.ProductBusinessLogic;
import com.javabootcamp.shoppingflow.model.entity.*;
import com.javabootcamp.shoppingflow.model.enums.OrderStatusType;
import com.javabootcamp.shoppingflow.model.request.ConfirmOrderBasketRequest;
import com.javabootcamp.shoppingflow.model.request.CreateBasketRequest;
import com.javabootcamp.shoppingflow.model.response.BasketSummaryResponse;
import com.javabootcamp.shoppingflow.repository.*;
import com.javabootcamp.shoppingflow.validation.BasketValidation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerTest {
    private static List<PaymentType> mockPaymentTypes;
    private static List<Product> mockProducts;
    private static Product mockProductSelected;
    private static Basket mockBasket;
    private static Basket mockBasketCheckOut;
    private static Basket mockBasketConfirmShipping;
    private static Customer customer;
    private static List<OrderStatus> orderStatuses;
    private static Basket mockBasketConfirmOrder;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @MockBean
    private PaymentTypeRepository paymentTypeRepository;
    @MockBean
    private ProductRepository productRepository;
    @Autowired
    private ProductBusinessLogic productBusinessLogic;
    @MockBean
    private BasketRepository basketRepository;
    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private BasketValidation basketValidation;
    @Autowired
    private BasketBusinessLogic basketBusinessLogic;


    private static List<PaymentType> CreatePaymentTypes() {
        List<PaymentType> paymentTypeList = new ArrayList<>();
        PaymentType paymentType1 = new PaymentType();
        paymentType1.setId(1);
        paymentType1.setDescription("เครดิต");
        PaymentType paymentType2 = new PaymentType();
        paymentType2.setId(2);
        paymentType2.setDescription("เดบิต");
        paymentTypeList.add(paymentType1);
        paymentTypeList.add(paymentType2);
        return paymentTypeList;
    }

    private static List<Product> CreateProducts() {
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Adidas NMD R1");
        product1.setProductType(CreateProductType());
        product1.setPrice(28900.0);
        product1.setDiscount(0.28);
        product1.setFullPrice(39900.0);

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Nike");
        product2.setPrice(9900.0);
        product2.setDiscount(0.34);
        product2.setFullPrice(15000.0);

        Product product3 = new Product();
        product3.setId(3);
        product3.setName("Adidas NMD POCA");
        product2.setPrice(9500.0);
        product2.setDiscount(0.50);
        product2.setFullPrice(18000.0);
        return Arrays.asList(product1, product2, product3);
    }

    private static ProductType CreateProductType() {
        ProductType productType = new ProductType();
        productType.setId(1);
        productType.setDescription("เสื้อ");
        productType.setProductSizes(CreateProductSizes());
        return productType;
    }

    private static List<ProductSize> CreateProductSizes() {
        List<ProductSize> productSizes = new ArrayList<>();
        ProductSize productSize1 = new ProductSize();
        productSize1.setId(1);
        productSize1.setDescription("34");
        ProductSize productSize2 = new ProductSize();
        productSize2.setId(2);
        productSize2.setDescription("35");
        productSizes.add(productSize1);
        productSizes.add(productSize2);
        return productSizes;
    }

    private static Customer CreateCustomer() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("Leo");
        customer.setLastName("Messi");
        customer.setAddress("408/13 อาคารพหลโยธินเพลส");
        customer.setPostalCode(24521);
        customer.setDistrict("ลาดสวาย");
        customer.setProvince("ปทุุมธานี");
        customer.setPhoneNumber("0890654212");
        customer.setCreatedAt(new Date(System.currentTimeMillis()));
        return customer;
    }

    private static Basket CreateBasket() {
        Basket basket = new Basket();
        basket.setId(1);
        basket.setCustomer(customer);
        basket.setBasketItem(CreateBasketItem());
        return basket;
    }

    private static BasketItem CreateBasketItem() {
        BasketItem basketItem = new BasketItem();
        basketItem.setProduct(mockProductSelected);
        basketItem.setItemSize("34");
        basketItem.setItemPrice(mockProductSelected.getFullPrice());
        basketItem.setItemNetPrice(mockProductSelected.getFullPrice());
        basketItem.setItemName(mockProductSelected.getName());
        basketItem.setItemDiscount(mockProductSelected.getDiscount());
        return basketItem;
    }

    private static Basket CreateBasketCheckOut() {
        Basket basket = CreateBasket();
        basket.setCustomer(customer);
        basket.setBasketItem(CreateBasketItem());
        basket.setBasketOrder(CreateBasketOrder());
        return basket;
    }

    private static BasketOrder CreateBasketOrder() {
        BasketOrder basketOrder = new BasketOrder();
        Optional<OrderStatus> orderStatusCheckout = orderStatuses.stream().filter(status -> status.getDescription().equals(OrderStatusType.CHECKOUT.getName())).findFirst();
        basketOrder.setOrderStatus(orderStatusCheckout.get());
        basketOrder.setOrderAmount(mockBasket.getBasketItem().getItemNetPrice());
        return basketOrder;
    }

    private static List<OrderStatus> CreateOrderStatus() {
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(new OrderStatus(1, "Checkout"));
        orderStatuses.add(new OrderStatus(2, "Confirm-Shipping"));
        orderStatuses.add(new OrderStatus(3, "Confirm-Order"));
        return orderStatuses;
    }

    private static Basket CreateBasketConfirmShipping() {
        Basket basket = CreateBasket();
        basket.setCustomer(customer);
        basket.setBasketItem(CreateBasketItem());
        BasketOrder basketOrder = CreateBasketOrder();
        Optional<OrderStatus> orderStatusConfirmShipping = orderStatuses.stream().filter(status -> status.getDescription().equals(OrderStatusType.CONFIRM_SHIPPING.getName())).findFirst();
        basketOrder.setOrderStatus(orderStatusConfirmShipping.get());
        basket.setBasketOrder(basketOrder);
        return basket;
    }


    private static Basket CreateBasketConfirmOrder() {
        Basket basket = CreateBasket();
        basket.setCustomer(customer);
        basket.setBasketItem(CreateBasketItem());
        BasketOrder basketOrder = CreateBasketOrder();
        basketOrder = CreateBasketOrderPayment(basketOrder);
        Optional<OrderStatus> orderStatusConfirmShipping = orderStatuses.stream().filter(status -> status.getDescription().equals(OrderStatusType.CONFIRM_ORDER.getName())).findFirst();
        basketOrder.setOrderStatus(orderStatusConfirmShipping.get());
        basket.setBasketOrder(basketOrder);
        return basket;
    }

    private static BasketOrder CreateBasketOrderPayment(BasketOrder basketOrder) {
        BasketPayment basketPayment = new BasketPayment();
        basketPayment.setCardNumber(123214);
        basketPayment.setCardOwnerName("Leo Messi");
        basketPayment.setCardExpiredMonth(4);
        basketPayment.setCardExpiredYear(2015);
        basketPayment.setCardCcvCvv(123);
        basketPayment.setCreatedAt(new Date(System.currentTimeMillis()));
        basketPayment.setPaymentType(mockPaymentTypes.get(1));

        basketOrder.setBasketPayment(basketPayment);
        basketOrder.setInvoiceNumber("12324");
        return basketOrder;
    }

    @BeforeAll
    static void setUpAllTest() {
        mockPaymentTypes = CreatePaymentTypes();
        mockProducts = CreateProducts();
        mockProductSelected = mockProducts.get(0);
        orderStatuses = CreateOrderStatus();
        customer = CreateCustomer();
        mockBasket = CreateBasket();
        mockBasketCheckOut = CreateBasketCheckOut();
        mockBasketConfirmShipping = CreateBasketConfirmShipping();
        mockBasketConfirmOrder = CreateBasketConfirmOrder();
    }


    @BeforeEach
    void setUpEachTest() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }


    @Test
    void contextLoads() {
    }

    @Test
    @Order(1)
    @DisplayName("Case GetProductsByName(Adidas NMD) [Search product by Name]")
    void caseGetProductsByNameAdidasNMD() {
        List<Product> mockProductAdidas = mockProducts.stream().filter(product -> product.getName().contains("Adidas NMD")).collect(Collectors.toList());
        when(productRepository.findByNameIgnoreCaseContaining("Adidas NMD")).thenReturn(mockProductAdidas  );

        Product[] products = testRestTemplate.getForObject("/api/products?name={name}", Product[].class,"Adidas NMD");

        assertEquals(products.length, 2);
    }

    @Test
    @Order(2)
    @DisplayName("Case GetProductId1 [Choose a product]")
    void caseGetProductId1() {
        when(productRepository.findById(1)).thenReturn(Optional.of(mockProductSelected));

        Product product = testRestTemplate.getForObject("/api/products/1", Product.class);

        assertEquals(product.getId(), 1);
        assertEquals(product.getName(), "Adidas NMD R1");
        assertNotNull(product.getProductType());
        assertEquals(product.getProductType().getProductSizes().size(), 2);
    }

    @Test
    @Order(3)
    @DisplayName("Case PostBasketWithSelectedProducts [Add product to basket]")
    void casePostBasketWithSelectedProducts() {
        ProductSize productSize = mockProductSelected.getProductType().getProductSizes().get(0);
        CreateBasketRequest createBasketRequest = new CreateBasketRequest(mockProductSelected.getId(), productSize.getDescription());
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1)).thenReturn(Optional.of(mockProductSelected));

        HttpHeaders headers = new HttpHeaders();
        headers.set("customerId", String.valueOf(customer.getId()));
        HttpEntity<CreateBasketRequest> request = new HttpEntity<>(createBasketRequest, headers);
        ResponseEntity<Basket> basketResposne = testRestTemplate.postForEntity("/api/baskets", request, Basket.class);

        Basket basket = basketResposne.getBody();
        assertNotNull(basket);
        assertNotNull(basket.getBasketItem());
        assertNotNull(basket.getCustomer());
        assertEquals(basket.getBasketItem().getItemName(), "Adidas NMD R1");
        assertEquals(basket.getCustomer().getName(), "Leo");
        assertEquals(basket.getCustomer().getPostalCode(), 24521);

    }

    @Test
    @Order(4)
    @DisplayName("Case GetBasket [Show data in basket]")
    void caseGetBasket() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(mockBasket));

        HttpHeaders headers = new HttpHeaders();
        headers.set("customerId", String.valueOf(customer.getId()));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Basket> basketResponse = testRestTemplate.exchange("/api/baskets/1", HttpMethod.GET, requestEntity, Basket.class);

        Basket basket = basketResponse.getBody();
        assertNotNull(basket);
        assertNotNull(basket.getBasketItem());
        assertNotNull(basket.getCustomer());
        assertEquals(basket.getBasketItem().getItemName(), "Adidas NMD R1");
        assertEquals(basket.getCustomer().getName(), "Leo");
        assertEquals(basket.getCustomer().getLastName(), "Messi");
        assertEquals(basket.getCustomer().getPostalCode(), 24521);
        assertEquals(basket.getCustomer().getAddress(), "408/13 อาคารพหลโยธินเพลส");
        assertEquals(basket.getCustomer().getDistrict(), "ลาดสวาย");
        assertEquals(basket.getCustomer().getProvince(), "ปทุุมธานี");
        assertEquals(basket.getCustomer().getPhoneNumber(), "0890654212");

    }

    @Test
    @Order(5)
    @DisplayName("Case CheckoutBasket [Checkout]")
    void caseCheckout() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(mockBasket));
        when(orderStatusRepository.findById(1)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Checkout")).findFirst().get()));
        when(basketRepository.save(any(Basket.class))).thenReturn(mockBasketCheckOut);

        HttpHeaders headers = new HttpHeaders();
        headers.set("customerId", String.valueOf(customer.getId()));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Basket> basketResponse = testRestTemplate.exchange("/api/baskets/1/checkout", HttpMethod.PATCH, requestEntity, Basket.class);

        Basket basket = basketResponse.getBody();
        assertNotNull(basket);
        assertNotNull(basket.getBasketItem());
        assertNotNull(basket.getCustomer());
        assertNotNull(basket.getBasketOrder());
        assertEquals(basket.getBasketOrder().getOrderStatus().getDescription(), "Checkout");

    }

    @Test
    @Order(6)
    @DisplayName("Case Confirm-Shipping Basket [Shipping]")
    void caseConfirmShipping() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(mockBasketCheckOut));
        when(orderStatusRepository.findById(2)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Confirm-Shipping")).findFirst().get()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("customerId", String.valueOf(customer.getId()));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Basket> basketResponse = testRestTemplate.exchange("/api/baskets/1/confirm-shipping", HttpMethod.PATCH, requestEntity, Basket.class);

        Basket basket = basketResponse.getBody();
        assertNotNull(basket);
        assertNotNull(basket.getBasketItem());
        assertNotNull(basket.getCustomer());
        assertNotNull(basket.getBasketOrder());
        assertEquals(basket.getBasketOrder().getOrderStatus().getDescription(), "Confirm-Shipping");

    }


    @Test
    @Order(7)
    @DisplayName("Case ยิง api get payment-types ต้องได้ค่า size ของ response = 2 (ส่วน Get ในข้อ 4) [Payment]")
    void caseApiGetPaymentTypes() {
        when(paymentTypeRepository.findAll()).thenReturn(mockPaymentTypes);

        PaymentType[] results = testRestTemplate.getForObject("/api/payment-types", PaymentType[].class);

        assertEquals(results.length, 2);

    }

    @Test
    @Order(8)
    @DisplayName("Case ConfirmOrder [Confirm to order]")
    void caseConfirmOrder() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(mockBasketConfirmShipping));
        when(orderStatusRepository.findById(3)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Confirm-Order")).findFirst().get()));
        when(paymentTypeRepository.findById(1)).thenReturn(Optional.ofNullable(mockPaymentTypes.get(1)));
        ConfirmOrderBasketRequest confirmOrderBasketRequest = new ConfirmOrderBasketRequest(1221345, "Nattapan Meepiean", 4, 2025, 124, 1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("customerId", String.valueOf(customer.getId()));
        HttpEntity<ConfirmOrderBasketRequest> requestEntity = new HttpEntity<ConfirmOrderBasketRequest>(confirmOrderBasketRequest, headers);
        ResponseEntity<Basket> basketResponse = testRestTemplate.exchange("/api/baskets/1/confirm-order", HttpMethod.PATCH, requestEntity, Basket.class);

        Basket basket = basketResponse.getBody();
        BasketOrder basketOrder = basket.getBasketOrder();
        assertNotNull(basket);
        assertNotNull(basket.getBasketItem());
        assertNotNull(basket.getCustomer());
        assertNotNull(basketOrder);
        assertEquals(basketOrder.getOrderStatus().getDescription(), "Confirm-Order");
        assertNotNull(basketOrder.getInvoiceNumber());
        assertNotNull(basketOrder.getBasketPayment());
        assertEquals(basketOrder.getBasketPayment().getCardNumber(), 1221345);
        assertEquals(basketOrder.getBasketPayment().getPaymentType().getDescription(), "เดบิต");
    }

    @Test
    @Order(9)
    @DisplayName("Case GetBasketSummary [Summary]")
    void caseGetBasketSummary() {
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(mockBasketConfirmOrder));

        HttpHeaders headers = new HttpHeaders();
        headers.set("customerId", String.valueOf(customer.getId()));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<BasketSummaryResponse> basketSummaryResponse = testRestTemplate.exchange("/api/baskets/1/summary", HttpMethod.GET, requestEntity, BasketSummaryResponse.class);

        BasketSummaryResponse basketSummary = basketSummaryResponse.getBody();
        assertNotNull(basketSummary.getInvoiceNumber());
        assertNotEquals(basketSummary.getInvoiceNumber(), "");
        assertEquals(basketSummary.getPayer(), "Leo Messi");
        assertNotNull(basketSummary.getTransactionDate());
        assertNotNull(basketSummary.getPaymentType());
        assertNotNull(basketSummary.getNetAmount());


    }


}