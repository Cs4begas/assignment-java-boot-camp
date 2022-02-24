package com.javabootcamp.shoppingflow.businessLogic;

import com.javabootcamp.shoppingflow.exception.NotFoundException;
import com.javabootcamp.shoppingflow.model.entity.Product;
import com.javabootcamp.shoppingflow.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductBusinessLogicTest {
    @InjectMocks
    ProductBusinessLogic productBusinessLogic;

    @Mock
    private ProductRepository productRepository;

    private List<Product> products;

    private Product product2;

    @BeforeEach
    public void setUp() {
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Adidas");
        product2 = new Product();
        product2.setId(2);
        product2.setName("Nike");
        products = new ArrayList<>(Arrays.asList(product1, product2));
    }

    @Test
    @DisplayName("Case Test Method getProductsByQueryParams ด้วย name = null ผลลัพธ์ results มีขนาดเท่ากับ 2")
    void caseGetProductsListWithoutRequestParams() {
        when(productRepository.findAll()).thenReturn(products);
        Optional<String> param = Optional.empty();
        List<Product> results = productBusinessLogic.getProductsByQueryParams(param);

        assertEquals(results.size(), 2);
    }

    @Test
    @DisplayName("Case Test Method getProductsByQueryParams ด้วย name = Nike ต้องได้ product ที่ id = 2 และ name = Nike")
    void caseGetProductsListWithRequestParamNameIsNike() {
        when(productRepository.findByNameIgnoreCaseContaining("Nike")).thenReturn(products);
        Optional<String> param = Optional.of("Nike");
        List<Product> results = productBusinessLogic.getProductsByQueryParams(param);
        Product result = results.get(1);

        assertEquals(result.getId(), 2);
        assertEquals(result.getName(), "Nike");

    }

    @Test
    @DisplayName("Case Test Method getProductsByQueryParams ด้วย name Pepsi ต้องได้ message Not found product name like Pepsi")
    void caseGetProductsListWithRequestParamNameIsPepsi() {
        when(productRepository.findByNameIgnoreCaseContaining("Pepsi")).thenReturn(List.of());
        Optional<String> param = Optional.of("Pepsi");
        assertThrows(NotFoundException.class, () -> productBusinessLogic.getProductsByQueryParams(param), "Not found product name like Pepsi");
    }

    @Test
    @DisplayName("Case Test Method getProductById ด้วย id = 2 ต้องได้ product ที่ id = 1 และ name = Nike")
    void caseGetWithIdIs1() {

        when(productRepository.findById(2)).thenReturn(Optional.ofNullable(product2));
        Product result = productBusinessLogic.getProductById(Optional.of(2));

        assertEquals(result.getId(), 2);
        assertEquals(result.getName(), "Nike");

    }

    @Test
    @DisplayName("Case Test Method getProductById ด้วย id = 3 ต้องได้ message Not found product id 3")
    void caseGetWithIdIs3() {

        when(productRepository.findById(3)).thenReturn(Optional.ofNullable(null));
        assertThrows(NotFoundException.class, () -> productBusinessLogic.getProductById(Optional.of(3)), "Not found product id 3");
    }


}