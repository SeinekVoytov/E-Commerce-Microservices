package org.example.productservice.elasticsearch.document;

public record ProductDocument(
        String id,
        String name,
        String description
) {
}