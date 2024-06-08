package org.example.orderservice.mapper.product;

import org.example.orderservice.dto.product.CategoryDto;
import org.example.orderservice.model.product.Category;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CategoryMapper {

    CategoryDto toDto(Category entity);

    Category toEntity(CategoryDto dto);

    Set<Category> toEntitiesSet(Set<CategoryDto> dtos);
}
