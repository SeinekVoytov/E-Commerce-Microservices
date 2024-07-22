package org.example.inventoryservice.mapper;

import org.example.inventoryservice.dto.InventoryItemResponse;
import org.example.inventoryservice.model.InventoryItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface InventoryItemMapper {

    InventoryItemResponse toResponse(InventoryItem entity);
}
