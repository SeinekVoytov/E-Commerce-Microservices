package org.example.orderservice.communication;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.cart.CartContentResponse;
import org.example.orderservice.dto.product.ProductDetailsDto;
import org.example.orderservice.exception.AccessTokenExpiredException;
import org.example.orderservice.exception.CartNotFoundException;
import org.example.orderservice.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceCommunicator {

    private final RestTemplate restTemplate;

    @Value("${USER_SERVICE_BASE_URL}")
    private String baseUrl;

    public CartContentResponse getCartContent(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        String url = String.format("%s/cart/items", baseUrl);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, httpEntity, CartContentResponse.class).getBody();
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case HttpStatus.UNAUTHORIZED -> throw new AccessTokenExpiredException();
                case HttpStatus.NOT_FOUND -> throw new CartNotFoundException();
                default -> throw new IllegalStateException("Unexpected value: " + e.getStatusCode());
            }
        }
    }

    public List<ProductDetailsDto> getProductsByIds(List<Integer> ids) {
        String url =
                String.format("%s/products/bulk?ids=%s", baseUrl, listToQueryParam(ids));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProductDetailsDto>>() {}
            ).getBody();
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
