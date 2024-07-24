package org.example.productservice.communication.service;

import lombok.RequiredArgsConstructor;
import org.example.productservice.communication.dto.InventoryItemRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class InventoryServiceCommunicator {

    private final RestTemplate restTemplate;

    @Value("${INVENTORY_SERVICE_BASE_URL}")
    private String baseUrl;

    public void requestNewInventoryItem(int productId, int quantity, String accessToken) {
        InventoryItemRequest request = new InventoryItemRequest(productId, quantity);
        HttpHeaders httpEntity = getAuthorizationHttpHeaders(accessToken);
        HttpEntity<InventoryItemRequest> requestEntity = new HttpEntity<>(request, httpEntity);
        String url = String.format("%s/inventory/items", baseUrl);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    private HttpHeaders getAuthorizationHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
