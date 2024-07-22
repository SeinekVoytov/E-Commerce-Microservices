package org.example.inventoryservice.mapper;

import org.example.inventoryservice.dto.InventoryResponse;
import org.example.inventoryservice.model.InventoryItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = InventoryItemMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface InventoryContentMapper {

    @Mapping(source = "items", target = "inventoryItems")
    InventoryResponse toResponse(List<InventoryItem> items, Integer totalItems);
}
