package org.example.productservice.elasticsearch.mapper;

import org.example.productservice.elasticsearch.document.ProductDocument;
import org.example.productservice.model.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductDocumentMapper {

    ProductDocument toDocument(Product product);
}
