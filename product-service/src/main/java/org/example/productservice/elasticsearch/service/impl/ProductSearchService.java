package org.example.productservice.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.example.productservice.elasticsearch.document.Document;
import org.example.productservice.elasticsearch.document.ProductDocument;
import org.example.productservice.elasticsearch.exception.ProductUpdatingException;
import org.example.productservice.elasticsearch.exception.SearchTextIsTooShortException;
import org.example.productservice.elasticsearch.mapper.ProductDocumentMapper;
import org.example.productservice.elasticsearch.service.AbstractElasticSearchService;
import org.example.productservice.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProductSearchService extends AbstractElasticSearchService<Product> {

    private static final String PRODUCT_INDEX = "product";

    private final ElasticsearchClient client;
    private final ProductDocumentMapper productDocumentMapper;

    @Override
    public Function<Product, Document> getMapper() {
        return productDocumentMapper::toDocument;
    }

    @Override
    protected String getIndexName() {
        return PRODUCT_INDEX;
    }

    @Override
    protected ElasticsearchClient getClient() {
        return client;
    }

    @Override
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