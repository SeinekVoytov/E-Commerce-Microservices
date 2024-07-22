package org.example.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.dto.InventoryItemRequest;
import org.example.inventoryservice.dto.InventoryItemResponse;
import org.example.inventoryservice.dto.InventoryResponse;
import org.example.inventoryservice.dto.UpdateInventoryItemQuantityRequest;
import org.example.inventoryservice.exception.InventoryItemAlreadyExistsException;
import org.example.inventoryservice.exception.InventoryItemNotFoundException;
import org.example.inventoryservice.exception.NotSufficientAmountOfProductException;
import org.example.inventoryservice.mapper.InventoryContentMapper;
import org.example.inventoryservice.mapper.InventoryItemMapper;
import org.example.inventoryservice.model.InventoryItem;
import org.example.inventoryservice.repository.InventoryItemRepository;
import org.example.inventoryservice.service.InventoryItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    private final InventoryItemMapper inventoryItemMapper;
    private final InventoryContentMapper inventoryContentMapper;

    @Override
    public InventoryItemResponse getInventoryItemByProductId(Integer productId) {
        InventoryItem foundItem = inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryItemNotFoundException(productId));

        return inventoryItemMapper.toResponse(foundItem);
    }

    @Override
    public InventoryResponse getAllInventory() {
        List<InventoryItem> inventoryItems = inventoryItemRepository.findAll();
        return inventoryContentMapper.toResponse(
                inventoryItems,
                inventoryItems.size()
        );
    }

    @Override
    public InventoryItemResponse addInventoryItem(InventoryItemRequest request) {

        if (inventoryItemRepository.existsByProductId(request.productId())) {
            throw new InventoryItemAlreadyExistsException(request.productId());
        }

        InventoryItem newItem = inventoryItemMapper.toEntity(request);
        newItem = inventoryItemRepository.save(newItem);
        return inventoryItemMapper.toResponse(newItem);
    }

    @Override
    public InventoryItemResponse updateInventoryItemQuantity(Integer productId,
                                                             UpdateInventoryItemQuantityRequest request) {

        InventoryItem itemToBeUpdated = inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryItemNotFoundException(productId));

        switch (request.operationType()) {
            case INCREMENT -> {
                int currQuantity = itemToBeUpdated.getQuantity();
                itemToBeUpdated.setQuantity(currQuantity + request.quantity());
            }
            case DECREMENT -> {
                int currQuantity = itemToBeUpdated.getQuantity();
                if (currQuantity < request.quantity()) {
                    throw new NotSufficientAmountOfProductException(request.quantity(), currQuantity);
                }
                itemToBeUpdated.setQuantity(currQuantity - request.quantity());
            }
            case SET -> itemToBeUpdated.setQuantity(request.quantity());
        }

        itemToBeUpdated = inventoryItemRepository.save(itemToBeUpdated);
        return inventoryItemMapper.toResponse(itemToBeUpdated);
    }

    @Override
    public InventoryItemResponse deleteInventoryItem(Integer productId) {

        InventoryItem itemToBeDeleted = inventoryItemRepository.findByProductId(productId)
                        .orElseThrow(() -> new InventoryItemNotFoundException(productId));

        inventoryItemRepository.deleteById(productId);
        return inventoryItemMapper.toResponse(itemToBeDeleted);
    }
}
