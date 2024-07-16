package org.example.userservice.communication;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.product.ProductDetailsDto;
import org.example.userservice.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ProductServiceCommunicator {

    private final RestTemplate restTemplate;

    @Value("${PRODUCT_SERVICE_BASE_URL}")
    private String baseUrl;

    public ProductDetailsDto getProductById(Integer id) {
        String url = String.format("%s/products/%s", baseUrl, id);
        try {
            return restTemplate.getForObject(url, ProductDetailsDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(id);
            }

            throw new IllegalStateException();
        }
    }
}
