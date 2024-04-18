package org.example.productservice.service;

import org.example.productservice.dto.CreateProductDto;
import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductLongDto;

public interface ProductService {

    PageProductShortDto getAllShortProduct(int pageNo, int pageSize, String order);
    ProductLongDto getById(int id);
    ProductLongDto createProduct(CreateProductDto newProductData);
    void deleteById(int id);
    ProductLongDto updateProduct(int id, ProductLongDto updatedProduct);
}
