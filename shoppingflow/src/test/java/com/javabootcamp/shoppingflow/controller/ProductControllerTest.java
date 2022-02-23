package com.javabootcamp.shoppingflow.controller;

import com.javabootcamp.shoppingflow.model.entity.Product;
import com.javabootcamp.shoppingflow.repository.ProductRepository;
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
class ProductControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private ProductRepository productRepository;

    private List<Product> products;

    @BeforeEach
    public void setUp() {
        products = CreateMockListProducts();
    }

    private List<Product> CreateMockListProducts(){
        List<Product> productList;
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Adidas");
        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Nike");
        productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);
        return productList;
    }

    @Test
    @DisplayName("Case ยิง api get prodcuts ต้องได้ค่า size ของ response = 2")
    void caseApiGetPaymentTypes() {
        when(productRepository.findAll()).thenReturn(products);
        Product[] results = testRestTemplate.getForObject("/api/products", Product[].class);
        assertEquals(results.length, 2);

    }
}