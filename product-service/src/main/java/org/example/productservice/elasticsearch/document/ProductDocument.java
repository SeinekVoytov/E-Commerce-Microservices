package org.example.productservice.elasticsearch.document;

public record ProductDocument(
        Integer id,
        String name,
        String description
) {
}