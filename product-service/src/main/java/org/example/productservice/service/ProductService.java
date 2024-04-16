package org.example.productservice.service;

import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductLongDto;

public interface ProductService {

    PageProductShortDto getAllShortProduct(int pageNo, int pageSize);
    ProductLongDto getById(int id);
    void deleteProductById(int id);
    ProductLongDto updateProduct(int id, ProductLongDto updatedProduct);
}
