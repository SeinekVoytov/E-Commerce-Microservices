package org.example.productservice.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.example.productservice.elasticsearch.document.Document;
import org.example.productservice.elasticsearch.exception.ElasticSearchIndexingException;
import org.example.productservice.elasticsearch.exception.ElasticSearchUpdatingException;

import java.io.IOException;

public abstract class AbstractElasticSearchService<Model>
        implements ElasticSearchService<Model, Document> {

    protected abstract String getIndexName();

    protected abstract ElasticsearchClient getClient();

    @Override
    public void save(Model m) {
        try {
            Document document = getMapper().apply(m);
            getClient().index(i -> i
                    .index(getIndexName())
                    .id(String.valueOf(document.id()))
                    .document(document)
            );
        } catch (IOException e) {
            throw new ElasticSearchIndexingException(e);
        }
    }

    @Override
    public void update(Model updated) {
        try {
            Document updatedDoc = getMapper().apply(updated);
            getClient().update(u -> u
                            .index(getIndexName())
                            .id(String.valueOf(updatedDoc.id()))
                            .doc(updatedDoc)
                            .docAsUpsert(true),
                    updatedDoc.getClass());
        } catch (IOException e) {
            throw new ElasticSearchUpdatingException(e);
        }
    }
}
