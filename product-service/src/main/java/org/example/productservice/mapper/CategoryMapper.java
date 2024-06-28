package org.example.productservice.mapper;

import org.example.productservice.dto.CategoryDto;
import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.dto.CategoryWithParentDto;
import org.example.productservice.model.Category;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CategoryMapper {

    CategoryDto toDto(Category entity);

    Category toEntity(CategoryDto dto);

    Set<Category> toEntitiesSet(Set<CategoryDto> dtos);

    @Mapping(source = "childCategories", target = "children", qualifiedByName = "mapChildren")
    CategoryWithChildrenDto toDtoWithChildren(Category entity);

    Set<CategoryWithChildrenDto> toDtoWithChildrenSet(Set<Category> entities);

    @Named("mapChildren")
    default Set<CategoryWithChildrenDto> mapChildren(Set<Category> entities) {

        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDtoWithChildren)
                .collect(Collectors.toSet());
    }

    @Mapping(source = "parentCategory", target = "parent", qualifiedByName = "mapParent")
    CategoryWithParentDto toDtoWithParent(Category entity);

    @Named("mapParent")
    default CategoryWithParentDto mapParent(Category entity) {

        if (entity == null) {
            return null;
        }

        return new CategoryWithParentDto(
                entity.getId(),
                entity.getName(),
                mapParent(entity.getParentCategory())
        );
    }
}