package org.example.productservice.service;

import java.util.List;

public interface SearchService<DTO> {

    List<DTO> search(String keyword);

    List<DTO> reindex();
}
