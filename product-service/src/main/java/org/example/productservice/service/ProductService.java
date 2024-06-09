package org.example.productservice.service;

import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductDto> getAllShortProduct(Pageable pageable);

    ProductDetailsDto getById(int id);

    ProductDetailsDto createProduct(RequestProductDto newProductData);

    ProductDetailsDto deleteById(int id);

    ProductDetailsDto updateProduct(int id, RequestProductDto updatedProduct);
}
