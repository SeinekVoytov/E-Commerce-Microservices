package org.example.productservice.service.impl;

import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductLongDto;
import org.example.productservice.dto.ProductShortDto;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.model.ProductLong;
import org.example.productservice.model.ProductShort;
import org.example.productservice.repository.ProductLongRepository;
import org.example.productservice.repository.ProductShortRepository;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductShortRepository productShortRepository;
    private final ProductLongRepository productLongRepository;

    @Autowired
    public ProductServiceImpl(ProductShortRepository productShortRepository, ProductLongRepository productLongRepository) {
        this.productShortRepository = productShortRepository;
        this.productLongRepository = productLongRepository;
    }

    @Override
    public PageProductShortDto getAllShortProduct(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductShort> shortProducts = productShortRepository.findAll(pageable);

        List<ProductShortDto> pageContent = shortProducts.getContent().stream()
                .map(this::mapToShortDto)
                .toList();

        return PageProductShortDto.builder()
                .content(pageContent)
                .pageNo(shortProducts.getNumber())
                .pageSize(shortProducts.getSize())
                .totalElements(shortProducts.getTotalElements())
                .totalPages(shortProducts.getTotalPages())
                .build();
    }

    @Override
    public ProductLongDto getById(int id) {
        ProductLong productLong = productLongRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product could not be found"));
        return mapToLongDto(productLong);
    }

    @Override
    public void deleteProductById(int id) {
        ProductLong productToBeDeleted = productLongRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product could not be deleted"));
        productLongRepository.delete(productToBeDeleted);
    }

    @Override
    public ProductLongDto updateProduct(int id, ProductLongDto updatedProduct) {
        ProductLong productToBeUpdated = productLongRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product could not be updated"));
        updateProduct(productToBeUpdated, updatedProduct);
        return mapToLongDto(productLongRepository.save(productToBeUpdated));
    }

    private void updateProduct(ProductLong toBeUpdated, ProductLongDto updated) {

        toBeUpdated.setLengthInM(updated.getLengthInM());
        toBeUpdated.setWidthInM(updated.getWidthInM());
        toBeUpdated.setHeightInM(updated.getHeightInM());
        toBeUpdated.setNetWeightInKg(updated.getNetWeightInKg());
        toBeUpdated.setGrossWeightInKg(updated.getGrossWeightInKg());

        ProductShort insideProduct = toBeUpdated.getProductShort();
        insideProduct.setName(updated.getName());
        insideProduct.setImgUrls(updated.getImgUrls());
        insideProduct.setPrice(updated.getPrice());
        insideProduct.setCategories(updated.getCategories());
    }

    private ProductShortDto mapToShortDto(ProductShort productShort) {
        return ProductShortDto.builder()
                .id(productShort.getId())
                .name(productShort.getName())
                .imgUrls(productShort.getImgUrls())
                .price(productShort.getPrice())
                .categories(productShort.getCategories())
                .build();
    }

    private ProductLongDto mapToLongDto(ProductLong productLong) {
        return ProductLongDto.builder()
                .id(productLong.getId())
                .name(productLong.getProductShort().getName())
                .imgUrls(productLong.getProductShort().getImgUrls())
                .price(productLong.getProductShort().getPrice())
                .categories(productLong.getProductShort().getCategories())
                .lengthInM(productLong.getLengthInM())
                .widthInM(productLong.getWidthInM())
                .heightInM(productLong.getHeightInM())
                .netWeightInKg(productLong.getNetWeightInKg())
                .grossWeightInKg(productLong.getGrossWeightInKg())
                .build();
    }
}
