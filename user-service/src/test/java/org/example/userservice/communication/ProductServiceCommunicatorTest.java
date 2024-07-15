package org.example.userservice.communication;

import org.example.userservice.dto.product.PriceDto;
import org.example.userservice.dto.product.ProductDetailsDto;
import org.example.userservice.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceCommunicatorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductServiceCommunicator communicator;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(communicator, "baseUrl", "https://some-url.com");
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductFoundById() {

        Integer productId = 1;
        ProductDetailsDto expectedProduct = new ProductDetailsDto(
                productId,
                "name",
                "description",
                "brand",
                "countryManufacturer",
                Set.of("image_url"),
                new PriceDto(BigDecimal.TWO, "UAH"),
                Collections.emptyList(),
                1.0,
                1.0,
                1.0,
                1.0,
                1.0
        );

        when(restTemplate.getForObject(anyString(), eq(ProductDetailsDto.class)))
                .thenReturn(expectedProduct);

        ProductDetailsDto actualProduct = communicator.getProductById(productId);

        assertAll(
                () -> assertNotNull(actualProduct),
                () -> assertEquals(expectedProduct, actualProduct)
        );

        verify(restTemplate).getForObject(anyString(), eq(ProductDetailsDto.class));
    }

    @Test
    void getProductById_ShouldThrowProductNotFoundExc_When404StatusReceived() {

        Integer productId = 1;
        when(restTemplate.getForObject(anyString(), eq(ProductDetailsDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(
                ProductNotFoundException.class,
                () -> communicator.getProductById(productId)
        );

        verify(restTemplate).getForObject(anyString(), eq(ProductDetailsDto.class));
    }
    
    @Test
    void getProductById_ShouldThrowIllegalStateExc_WhenNot404ErrorStatusReceived() {

        Integer productId = 1;
        when(restTemplate.getForObject(anyString(), eq(ProductDetailsDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(
                IllegalStateException.class,
                () -> communicator.getProductById(productId)
        );

        verify(restTemplate).getForObject(anyString(), eq(ProductDetailsDto.class));
    }
}
