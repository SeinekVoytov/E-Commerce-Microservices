package org.example.productservice.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.example.productservice.elasticsearch.document.ProductDocument;
import org.example.productservice.elasticsearch.exception.ProductIndexingException;
import org.example.productservice.elasticsearch.exception.ProductUpdatingException;
import org.example.productservice.elasticsearch.mapper.ProductDocumentMapper;
import org.example.productservice.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private static final String PRODUCT_INDEX = "product";

    private final ElasticsearchClient client;
    private final ProductDocumentMapper productDocumentMapper;

    public void save(Product product) {
        try {
            ProductDocument document = productDocumentMapper.toDocument(product);
            client.index(i ->
                    i.index(PRODUCT_INDEX)
                            .id(document.id())
                            .document(document)
            );
        } catch (IOException e) {
            throw new ProductIndexingException();
        }
    }

    public void update(Product updated) {
        try {
            ProductDocument updatedDoc = productDocumentMapper.toDocument(updated);

            client.update(u -> u
                            .index(PRODUCT_INDEX)
                            .id(updatedDoc.id())
                            .doc(updatedDoc)
                            .docAsUpsert(true),
                    ProductDocument.class);
        } catch (IOException e) {
            throw new ProductUpdatingException();
        }
    }
}