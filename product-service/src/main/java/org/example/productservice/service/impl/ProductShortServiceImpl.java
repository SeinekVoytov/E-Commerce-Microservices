package org.example.productservice.service.impl;

import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductShortDto;
import org.example.productservice.model.ProductShort;
import org.example.productservice.repository.ProductShortRepository;
import org.example.productservice.service.ProductShortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductShortServiceImpl implements ProductShortService {

    private final ProductShortRepository productShortRepository;

    @Autowired
    public ProductShortServiceImpl(ProductShortRepository productShortRepository) {
        this.productShortRepository = productShortRepository;
    }

    @Override
    public PageProductShortDto getAllShortProduct(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductShort> shortProducts = productShortRepository.findAll(pageable);

        List<ProductShortDto> pageContent = shortProducts.getContent().stream()
                .map(this::mapToDto)
                .toList();

        return PageProductShortDto.builder()
                .content(pageContent)
                .pageNo(shortProducts.getNumber())
                .pageSize(shortProducts.getSize())
                .totalElements(shortProducts.getTotalElements())
                .totalPages(shortProducts.getTotalPages())
                .build();
    }

    private ProductShortDto mapToDto(ProductShort productShort) {
        return ProductShortDto.builder()
                .id(productShort.getId())
                .name(productShort.getName())
                .imgUri(productShort.getImgUri())
                .price(productShort.getPrice())
                .categories(productShort.getCategories())
                .build();
    }
}
