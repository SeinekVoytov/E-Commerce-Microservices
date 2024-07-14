package org.example.productservice.elasticsearch.document;

public record CategoryDocument(
        Integer id,
        String name
) implements Document {
}
