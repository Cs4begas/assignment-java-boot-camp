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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    private OrderStatusRepository orderStatusRepository;
    @Mock
    private PaymentTypeRepository paymentTypeRepository;

    private BasketBusinessLogic basketBusinessLogic;

    private Customer customer;

    private Product product;

    private Basket basket;

    private BasketItem basketItem;

    private Basket basketCheckout;

    private Basket basketConfirmShipping;

    private ConfirmOrderBasketRequest confirmOrderBasketRequest;

    private PaymentType paymentType;

    private List<OrderStatus> orderStatuses;

    private Basket basketConfirmOrder;

    @BeforeEach
    public void setUp() {
        basketBusinessLogic = new BasketBusinessLogic(basketRepository, customerRepository, productRepository, orderStatusRepository, paymentTypeRepository, basketValidation);
        customer = CreateCustomer();
        product = CreateProduct();
        basketItem = CreateBasketItem();
        basket = CreateBasket();
        orderStatuses = CreateOrderStatus();
        basketCheckout = CreateBasketCheckout();
        basketConfirmShipping = CreateBasketConfirmShipping();
        confirmOrderBasketRequest = CreateConfirmBasketRequest();
        paymentType = CreatePaymentType();
        basketConfirmOrder = CreateBasketConfirmOrder();
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

    private List<OrderStatus> CreateOrderStatus() {
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(new OrderStatus(1, "Checkout"));
        orderStatuses.add(new OrderStatus(2, "Confirm-Shipping"));
        orderStatuses.add(new OrderStatus(3, "Confirm-Order"));
        return orderStatuses;
    }

    private BasketOrder CreateBasketOrder() {
        BasketOrder basketOrder = new BasketOrder();
        basketOrder.setOrderAmount(10000.0);
        return basketOrder;
    }

    private Basket CreateBasketCheckout() {
        Basket basket = new Basket();
        basket.setId(1);
        basket.setCustomer(customer);
        basket.setBasketItem(basketItem);
        basket.setBasketOrder(CreateBasketOrder());
        OrderStatus checkOutOrderStatus = orderStatuses.stream().filter(orderStatus -> orderStatus.getDescription() == "Checkout").findFirst().get();
        basket.getBasketOrder().setOrderStatus(checkOutOrderStatus);
        return basket;
    }

    private Basket CreateBasketConfirmShipping() {
        Basket basket = new Basket();
        basket.setId(1);
        basket.setCustomer(customer);
        basket.setBasketItem(basketItem);
        basket.setBasketOrder(CreateBasketOrder());
        OrderStatus checkOutOrderStatus = orderStatuses.stream().filter(orderStatus -> orderStatus.getDescription() == "Confirm-Shipping").findFirst().get();
        basket.getBasketOrder().setOrderStatus(checkOutOrderStatus);
        return basket;
    }

    private ConfirmOrderBasketRequest CreateConfirmBasketRequest() {
        ConfirmOrderBasketRequest confirmOrderBasketRequest = new ConfirmOrderBasketRequest();
        confirmOrderBasketRequest.setCardNumber(1221345);
        confirmOrderBasketRequest.setCardOwnerName("Leo Messi");
        confirmOrderBasketRequest.setCardExpiredMonth(4);
        confirmOrderBasketRequest.setCardExpiredYear(2025);
        confirmOrderBasketRequest.setCardCcvCvv(124);
        confirmOrderBasketRequest.setPaymentTypeId(1);
        return confirmOrderBasketRequest;
    }

    private PaymentType CreatePaymentType() {
        PaymentType paymentType = new PaymentType();
        paymentType.setId(1);
        paymentType.setDescription("บัตรเครดิต");
        return paymentType;
    }

    private Basket CreateBasketConfirmOrder() {
        Basket basket = new Basket();
        basket.setId(1);
        basket.setCustomer(customer);
        basket.setBasketItem(basketItem);

        BasketOrder basketOrder = CreateBasketOrder();
        basketOrder = CreateBasketOrderPayment(basketOrder);
        basketOrder.setInvoiceNumber("122485");
        OrderStatus confirmOrderStatus = orderStatuses.stream().filter(orderStatus -> orderStatus.getDescription() == "Confirm-Order").findFirst().get();
        basketOrder.setOrderStatus(confirmOrderStatus);
        basket.setBasketOrder(basketOrder);
        return basket;

    }
    private BasketOrder CreateBasketOrderPayment(BasketOrder basketOrder){
        BasketPayment basketPayment = new BasketPayment();
        basketPayment.setCardNumber(123214);
        basketPayment.setCardOwnerName("Leo Messi");
        basketPayment.setCardExpiredMonth(4);
        basketPayment.setCardExpiredYear(2015);
        basketPayment.setCardCcvCvv(123);
        basketPayment.setCreatedAt(new Date(System.currentTimeMillis()));
        basketPayment.setPaymentType(paymentType);

        basketOrder.setBasketPayment(basketPayment);
        return basketOrder;
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
        Optional<Integer> customerId = Optional.empty();
        Optional<Integer> basketId = Optional.empty();

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.GetBasket(customerId, basketId));

        assertEquals(exception.getMessage(), "Please insert customerId on header");
    }

    @Test
    @DisplayName("Case Validate Test Method GetBasket ด้วย customerId = 1 และ basketId = null ต้องได้ message Please insert id value in path parameters")
    void caseGetBasketWithBasketIdIsNull() {
        Optional<Integer> customerId = Optional.ofNullable(1);
        Optional<Integer> basketId = Optional.empty();

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.GetBasket(customerId, basketId));

        assertEquals(exception.getMessage(), "Please insert id value in path parameters");
    }

    @Test
    @DisplayName("Case NotFound Test Method GetBasket ด้วย customerId = 3 และ basketId = 1 ต้องได้ message Not found customerId 3")
    void caseGetBasketWithCustomerIdIs3() {
        Optional<Integer> customerId = Optional.ofNullable(3);
        Optional<Integer> basketId = Optional.ofNullable(1);

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.GetBasket(customerId, basketId));

        assertEquals(exception.getMessage(), "Not found customerId 3");
    }

    @Test
    @DisplayName("Case NotFound Test Method GetBasket ด้วย customerId = 1 และ basketId = 5 ต้องได้ message Not found basketId 5")
    void caseGetBasketWithBasketIdIs5() {
        Optional<Integer> customerId = Optional.ofNullable(1);
        Optional<Integer> basketId = Optional.ofNullable(5);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.GetBasket(customerId, basketId));

        assertEquals(exception.getMessage(), "Customer's basket is not found");
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

    @Test
    @DisplayName("Case Validate Test Method HandleBasket ด้วย customerId = null และ basketId = null ต้องได้ message Please insert customerId on header")
    void caseHandleBasketWithCustomerIdIsNull() {
        Optional<Integer> customerId = Optional.empty();
        Optional<Integer> basketId = Optional.empty();

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CHECKOUT, null));

        assertEquals(exception.getMessage(), "Please insert customerId on header");
    }

    @Test
    @DisplayName("Case Validate Test Method HandleBasket ด้วย customerId = 1 และ basketId = null ต้องได้ message Please insert id value in path parameters")
    void caseHandleBasketWithBasketIdIsNull() {
        Optional<Integer> customerId = Optional.ofNullable(1);
        Optional<Integer> basketId = Optional.empty();

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CHECKOUT, null));

        assertEquals(exception.getMessage(), "Please insert id value in path parameters");
    }

    @Test
    @DisplayName("Case NotFound Test Method HandleBasket ด้วย customerId = 3, basketId = 2, OrderStatusType = CHECKOUT ต้องได้ message = Not found customerId 4")
    void caseHandleBasketWithCustomerIdIs4() {
        Optional<Integer> customerId = Optional.ofNullable(3);
        Optional<Integer> basketId = Optional.ofNullable(1);

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CHECKOUT, null));

        assertEquals(exception.getMessage(), "Not found customerId 3");
    }

    @Test
    @DisplayName("Case Validate Test Method HandleBasket ด้วย customerId = 1, basketId = 2, OrderStatusType = CHECKOUT ต้องได้ message = Customer's basket is invalid")
    void caseHandleBasketWithCustomerIdIs1AndBasketId2() {
        Optional<Integer> customerId = Optional.ofNullable(1);
        Optional<Integer> basketId = Optional.ofNullable(2);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CHECKOUT, null));
        assertEquals(exception.getMessage(), "Customer's basket is not found");
    }

    @Test
    @DisplayName("Case Validate Test Method HandleBasketOrder ด้วย customerId = 1 และ basketId 1 OrderStatusType เป็น CHECKOUT และ ConfirmOrderBasketRequest = null ต้องได้ message = Basket is already checkout")
    void caseHandleBasketAlreadyCheckout() { //Fix
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basketCheckout));
        when(orderStatusRepository.findById(1)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Checkout")).findFirst().get()));


        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CHECKOUT, null));
        assertEquals(exception.getMessage(), "Basket is already checkout");
    }

    @Test
    @DisplayName("Case Success Test Method HandleBasketOrder ด้วย customerId = 1 และ basketId 1 OrderStatusType เป็น CHECKOUT และ ConfirmOrderBasketRequest = null ต้องได้ Object Basket ที่ BasketOrder != null และ BasketOrder.OrderStatus = Checkout")
    void caseHandleBasketCheckoutSuccess() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basket));
        when(orderStatusRepository.findById(1)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Checkout")).findFirst().get()));

        Basket basketResult = basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CHECKOUT, null);

        assertNotNull(basketResult);
        assertNotNull(basketResult.getBasketOrder());
        assertEquals(basketResult.getBasketOrder().getOrderStatus().getDescription(), "Checkout");
    }

    @Test
    @DisplayName("Case Success Test Method HandleBasketOrder ด้วย customerId = 1 และ basketId 1 OrderStatusType เป็น CONFIRM_SHIPPING และ ConfirmOrderBasketRequest = null ต้องได้ message = Basket status is not Checkout")
    void caseHandleBasketShippingStatusNotEqualCheckout() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basketConfirmShipping));
        when(orderStatusRepository.findById(2)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Checkout")).findFirst().get()));

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CONFIRM_SHIPPING, null));
        assertEquals(exception.getMessage(), "Basket status is not Checkout");
    }

    @Test
    @DisplayName("Case Success Test Method HandleBasketOrder ด้วย customerId = 1 และ basketId 1 OrderStatusType เป็น CONFIRM_SHIPPING และ ConfirmOrderBasketRequest = null ต้องได้ Object Basket ที่ BasketOrder.OrderStatus = Confirm-Shipping")
    void caseConfirmBasketShippingSuccess() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basketCheckout));
        when(orderStatusRepository.findById(2)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Confirm-Shipping")).findFirst().get()));

        Basket basketResult = basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CONFIRM_SHIPPING, null);

        assertEquals(basketResult.getBasketOrder().getOrderStatus().getDescription(), "Confirm-Shipping");
    }

    @Test
    @DisplayName("Case Validate Test Method HandleBasketOrder ด้วย customerId = 1 และ basketId 1 OrderStatusType เป็น CONFIRM_ORDER  และ ConfirmOrderBasketRequest = null ต้องได้ message = Basket status is not Confirm-Shipping")
    void caseConfirmBasketOrderNotEqualConfirmShipping() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basketCheckout));
        when(orderStatusRepository.findById(3)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Confirm-Order")).findFirst().get()));

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CONFIRM_ORDER, null));
        assertEquals(exception.getMessage(), "Basket status is not Confirm-Shipping");
    }

    @Test
    @DisplayName("Case NotFound Test Method HandleBasketOrder ด้วย customerId = 1 และ basketId 1 OrderStatusType เป็น CONFIRM_ORDER และ ConfirmOrderBasketRequest = confirmOrderBasketRequest ต้องได้ message = Not found PaymentTypeId 5")
    void caseConfirmBasketOrderNotFoundPaymentType() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        confirmOrderBasketRequest.setPaymentTypeId(5);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basketConfirmShipping));
        when(orderStatusRepository.findById(3)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Confirm-Order")).findFirst().get()));

        Exception exception = assertThrows(NotFoundException.class, () -> basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CONFIRM_ORDER, confirmOrderBasketRequest));
        assertEquals(exception.getMessage(), "Not found PaymentTypeId 5");
    }

    @Test
    @DisplayName("Case Success Test Method HandleBasketOrder ด้วย customerId = 1 และ basketId 1 OrderStatusType เป็น CONFIRM_ORDER และ ConfirmOrderBasketRequest = confirmOrderBasketRequest ต้องได้ Object Basket ที่ BasketOrder != null และ BasketOrder.OrderStatus = Confirm-Order และ BasketOrder.BasketPayment != null และ BasketOrder.BasketPayment.cardNumber = 1221345 และ ฺBasketOrder.BasketPayment.PaymentType.Description = บัตรเครดิต และ BasketOrder.invoiceNumber != null และ BasketOrder.invoiceNumber != empty string")
    void caseConfirmBasketOrderSuccess() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(basketRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.ofNullable(basketConfirmShipping));
        when(orderStatusRepository.findById(3)).thenReturn(Optional.of(orderStatuses.stream().filter(x -> x.getDescription().equals("Confirm-Order")).findFirst().get()));
        when(paymentTypeRepository.findById(1)).thenReturn(Optional.ofNullable(paymentType));

        Basket basketResult = basketBusinessLogic.HandleBasketOrder(customerId, basketId, OrderStatusType.CONFIRM_ORDER, confirmOrderBasketRequest);

        BasketOrder basketOrderResult = basketResult.getBasketOrder();
        BasketPayment basketPaymentResult = basketOrderResult.getBasketPayment();
        assertNotNull(basketResult);
        assertNotNull(basketOrderResult);
        assertEquals(basketResult.getBasketOrder().getOrderStatus().getDescription(), "Confirm-Order");
        assertNotNull(basketPaymentResult);
        assertEquals(basketPaymentResult.getCardNumber(),1221345);
        assertEquals(basketPaymentResult.getPaymentType().getDescription(),"บัตรเครดิต");
        assertNotNull(basketOrderResult);
        assertNotEquals(basketOrderResult.getInvoiceNumber(),"");
    }

    @Test
    @DisplayName("Case Test Method GenerateInvoiceNumber ต้องได้ String ที่มีขนาด = 6 และ String ไม่เท่ากับ empty string")
    void caseTestMethodGenerateInvoiceNumber() {
        String generateInvoiceNumber = basketBusinessLogic.GenerateInvoiceNumber();

        assertEquals(generateInvoiceNumber.length(),6);
        assertNotEquals(generateInvoiceNumber,"");
    }

    @Test
    @DisplayName("Case Validation Test Method GetBasketSummary ด้วย basket ที่ยังไม่ Checkout ต้องได้ message Basket status is not Checkout")
    void caseTestMethodGetBasketSummaryNotCheckout() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(basketRepository.findByIdAndCustomerId(customerId.get(), basketId.get())).thenReturn(Optional.ofNullable(basket));

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.GetBasketSummary(customerId, basketId));
        assertEquals(exception.getMessage(),"Basket status is not Checkout");
    }

    @Test
    @DisplayName("Case Validation Test Method GetBasketSummary ด้วย basket ที่ basketOrderStatus != Confirm-Order ต้องได้ message Basket status is not Confirm-Order")
    void caseTestMethodGetBasketSummaryNotConfirmOrder() {
        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(basketRepository.findByIdAndCustomerId(customerId.get(), basketId.get())).thenReturn(Optional.ofNullable(basketConfirmShipping));

        Exception exception = assertThrows(ValidationException.class, () -> basketBusinessLogic.GetBasketSummary(customerId, basketId));
        assertEquals(exception.getMessage(),"Basket status is not Confirm-Order");
    }

    @Test
    @DisplayName("Case Success Test Method GetBasketSummary ด้วย basket ที่ผ่านการ Confirm-Order แล้ว ต้องได้ Object BasketSummary ที่ invoiceNumber, payer = Messi, transactionDate มีค่า, paymentType มีค่า และ NetAmount มีค่า")
    void caseTestMethodGetBasketSummarySuccess() {

        Optional<Integer> customerId = Optional.of(1);
        Optional<Integer> basketId = Optional.ofNullable(1);
        when(basketRepository.findByIdAndCustomerId(customerId.get(), basketId.get())).thenReturn(Optional.ofNullable(basketConfirmOrder));

        BasketSummaryResponse basketSummaryResponse = basketBusinessLogic.GetBasketSummary(customerId, basketId);

        assertNotNull(basketSummaryResponse.getInvoiceNumber());
        assertNotEquals(basketSummaryResponse.getInvoiceNumber(), "");
        assertEquals(basketSummaryResponse.getPayer(), "Leo Messi");
        assertNotNull(basketSummaryResponse.getTransactionDate());
        assertNotNull(basketSummaryResponse.getPaymentType());
        assertNotNull(basketSummaryResponse.getNetAmount());
    }


}