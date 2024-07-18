package org.example.orderservice.communication;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.product.BulkProductsResponse;
import org.example.orderservice.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceCommunicator {

    private final RestTemplate restTemplate;

    @Value("${PRODUCT_SERVICE_BASE_URL}")
    private String baseUrl;

    public BulkProductsResponse getProductsByIds(List<Integer> ids) {
        String url =
                String.format("%s/products/bulk?ids=%s", baseUrl, listToQueryParam(ids));
        try {
            return restTemplate.getForObject(url, BulkProductsResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ProductNotFoundException(ids);
            }
            throw new IllegalStateException("Unexpected value: " + e.getStatusCode());
        }
    }

    private <T> String listToQueryParam(List<T> list) {
        return list.toString().replace("[", "").replace("]", "");
    }
}
