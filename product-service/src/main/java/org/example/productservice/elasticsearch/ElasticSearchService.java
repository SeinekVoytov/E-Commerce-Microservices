package org.example.productservice.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.example.productservice.elasticsearch.document.ProductDocument;
import org.example.productservice.elasticsearch.exception.ProductIndexingException;
import org.example.productservice.elasticsearch.exception.ProductUpdatingException;
import org.example.productservice.elasticsearch.exception.SearchTextIsTooShortException;
import org.example.productservice.elasticsearch.mapper.ProductDocumentMapper;
import org.example.productservice.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private static final String PRODUCT_INDEX = "product";

    private final ElasticsearchClient client;
    private final ProductDocumentMapper productDocumentMapper;

    public void save(Product product) {
        try {
            ProductDocument document = productDocumentMapper.toDocument(product);
            client.index(i -> i
                    .index(PRODUCT_INDEX)
                    .id(String.valueOf(document.id()))
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
                            .id(String.valueOf(updatedDoc.id()))
                            .doc(updatedDoc)
                            .docAsUpsert(true),
                    ProductDocument.class);
        } catch (IOException e) {
            throw new ProductUpdatingException();
        }
    }

    public List<Integer> search(String searchText) {

        if (searchText.length() < 3) {
            throw new SearchTextIsTooShortException();
        }

        try {
            SearchResponse<ProductDocument> response = client.search(s -> s
                    .index(PRODUCT_INDEX)
                    .query(q -> q
                            .multiMatch(m -> m
                                    .fields("name.fullText", "description.fullText")
                                    .query(searchText)
                                    .type(TextQueryType.MostFields)
                            )
                    ),
                    ProductDocument.class);

            return response.hits().hits().stream()
                    .map(hit -> Integer.parseInt(hit.id()))
                    .toList();

        } catch (IOException e) {
            throw new ProductUpdatingException();
        }
    }
}