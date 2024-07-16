package org.example.productservice.elasticsearch.mapper;

import org.example.productservice.elasticsearch.document.CategoryDocument;
import org.example.productservice.model.Category;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CategoryDocumentMapper {

    CategoryDocument toDocument(Category category);
}
