package org.example.inventoryservice.mapper;

import org.example.inventoryservice.dto.InventoryItemRequest;
import org.example.inventoryservice.dto.InventoryItemResponse;
import org.example.inventoryservice.model.InventoryItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface InventoryItemMapper {

    InventoryItemResponse toResponse(InventoryItem entity);

    @Mapping(target = "id", ignore = true)
    InventoryItem toEntity(InventoryItemRequest request);

    List<InventoryItemResponse> toResponseList(List<InventoryItem> entities);
}
