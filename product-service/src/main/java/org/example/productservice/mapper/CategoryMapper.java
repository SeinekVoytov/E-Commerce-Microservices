package org.example.productservice.mapper;

import org.example.productservice.dto.CategoryDto;
import org.example.productservice.model.Category;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;
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
