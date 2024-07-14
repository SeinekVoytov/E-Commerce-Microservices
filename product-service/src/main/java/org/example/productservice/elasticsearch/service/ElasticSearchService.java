package org.example.productservice.elasticsearch.service;

import java.util.List;
import java.util.function.Function;

public interface ElasticSearchService<Model, Document> {

    Function<Model, Document> getMapper();

    void save(Model m);

    void update(Model updated);

    List<Integer> search(String searchText);
}
