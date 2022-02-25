package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.model.entity.PaymentType;
import com.javabootcamp.shoppingflow.repository.PaymentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentTypeControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private PaymentTypeRepository paymentTypeRepository;

    private List<PaymentType> paymentTypes = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        paymentTypes = CreatePaymentTypes();
    }

    private List<PaymentType> CreatePaymentTypes() {
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

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Case ยิง api get payment-types ต้องได้ค่า size ของ response = 2")
    void caseApiGetPaymentTypes() {
        when(paymentTypeRepository.findAll()).thenReturn(paymentTypes);
        PaymentType[] results = testRestTemplate.getForObject("/api/payment-types", PaymentType[].class);
        assertEquals(results.length, 2);

    }

}