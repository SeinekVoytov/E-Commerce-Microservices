package org.example.productservice.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.example.productservice.elasticsearch.document.CategoryDocument;
import org.example.productservice.elasticsearch.document.Document;
import org.example.productservice.elasticsearch.exception.ProductUpdatingException;
import org.example.productservice.elasticsearch.exception.SearchTextIsTooShortException;
import org.example.productservice.elasticsearch.mapper.CategoryDocumentMapper;
import org.example.productservice.elasticsearch.service.AbstractElasticSearchService;
import org.example.productservice.model.Category;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CategorySearchService extends AbstractElasticSearchService<Category> {

    private static final String CATEGORY_INDEX = "category";

    private final ElasticsearchClient client;
    private final CategoryDocumentMapper categoryDocumentMapper;

    @Override
    public Function<Category, Document> getMapper() {
        return categoryDocumentMapper::toDocument;
    }

    @Override
    protected String getIndexName() {
        return CATEGORY_INDEX;
    }

    @Override
    protected ElasticsearchClient getClient() {
        return client;
    }

    @Override
    public List<Integer> search(String searchText) {

        if (searchText.length() < 2) {
            throw new SearchTextIsTooShortException();
        }

        try {
            SearchResponse<CategoryDocument> response = client.search(s -> s
                            .index(CATEGORY_INDEX)
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("name.fullText")
                                            .query(searchText)
                                            .type(TextQueryType.MostFields)
                                    )
                            ),
                    CategoryDocument.class);

            return response.hits().hits().stream()
                    .map(hit -> Integer.parseInt(hit.id()))
                    .toList();

        } catch (IOException e) {
            throw new ProductUpdatingException();
        }
    }
}
