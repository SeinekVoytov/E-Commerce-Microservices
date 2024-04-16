package org.example.productservice.service.impl;

import org.example.productservice.dto.CreateProductDto;
import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductLongDto;
import org.example.productservice.dto.ProductShortDto;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.model.*;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ProductLongRepository;
import org.example.productservice.repository.ProductShortRepository;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Set<String> AVAILABLE_SORT_PARAMETERS = Set.of("price.amount");
    private static final Set<String> AVAILABLE_ORDER = Set.of("asc", "desc");

    private final ProductShortRepository productShortRepository;
    private final ProductLongRepository productLongRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductShortRepository productShortRepository, ProductLongRepository productLongRepository, CategoryRepository categoryRepository) {
        this.productShortRepository = productShortRepository;
        this.productLongRepository = productLongRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PageProductShortDto getAllShortProduct(int pageNo, int pageSize, String order) {

        String[] sortingParams = order.trim().split(":");

        Pageable pageable = PageRequest.of(pageNo, pageSize, createSort(sortingParams[1], sortingParams[0]));
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

    @Override
    public ProductLongDto createProduct(CreateProductDto newProductData) {

        ProductLong createdProduct = new ProductLong();
        createdProduct.setLengthInM(newProductData.getLengthInM());
        createdProduct.setWidthInM(newProductData.getWidthInM());
        createdProduct.setHeightInM(newProductData.getHeightInM());
        createdProduct.setNetWeightInKg(newProductData.getNetWeightInKg());
        createdProduct.setGrossWeightInKg(newProductData.getGrossWeightInKg());

        ProductShort productShort = new ProductShort();
        productShort.setName(newProductData.getName());
        Iterable<Category> selectedCategories = categoryRepository.findAllById(newProductData.getCategoryIds());

        List<Category> categoriesToBeSaved = new ArrayList<>();
        for (Category selectedCategory : selectedCategories) {
            categoriesToBeSaved.add(selectedCategory);
        }

        productShort.setCategories(categoriesToBeSaved);

        Price priceToBeSaved = new Price();
        priceToBeSaved.setAmount(newProductData.getPriceAmount());
        priceToBeSaved.setCurrency(newProductData.getPriceCurrency());

        productShort.setPrice(priceToBeSaved);
        productShort.setImages(new ArrayList<>());
        createdProduct.setProductShort(productShort);

        return mapToLongDto(productLongRepository.save(createdProduct));
    }

    private void updateProduct(ProductLong toBeUpdated, ProductLongDto updated) {

        toBeUpdated.setLengthInM(updated.getLengthInM());
        toBeUpdated.setWidthInM(updated.getWidthInM());
        toBeUpdated.setHeightInM(updated.getHeightInM());
        toBeUpdated.setNetWeightInKg(updated.getNetWeightInKg());
        toBeUpdated.setGrossWeightInKg(updated.getGrossWeightInKg());

        ProductShort insideProduct = toBeUpdated.getProductShort();
        insideProduct.setName(updated.getName());
        insideProduct.setPrice(updated.getPrice());
        insideProduct.setCategories(updated.getCategories());
    }

    private Sort createSort(String order, String sortParam) {

        if (!AVAILABLE_ORDER.contains(order) || !AVAILABLE_SORT_PARAMETERS.contains(sortParam)) {
            throw new InvalidQueryParameterException("Invalid query parameter value");
        }

        if (order.equals("desc")) {
            return Sort.by(sortParam).descending();
        }

        return Sort.by(sortParam).ascending();
    }

    private ProductShortDto mapToShortDto(ProductShort productShort) {
        return ProductShortDto.builder()
                .id(productShort.getId())
                .name(productShort.getName())
                .images(unwrapUrls(productShort.getImages()))
                .price(productShort.getPrice())
                .categories(productShort.getCategories())
                .build();
    }

    private ProductLongDto mapToLongDto(ProductLong productLong) {
        return ProductLongDto.builder()
                .id(productLong.getId())
                .name(productLong.getProductShort().getName())
                .images(unwrapUrls(productLong.getProductShort().getImages()))
                .price(productLong.getProductShort().getPrice())
                .categories(productLong.getProductShort().getCategories())
                .lengthInM(productLong.getLengthInM())
                .widthInM(productLong.getWidthInM())
                .heightInM(productLong.getHeightInM())
                .netWeightInKg(productLong.getNetWeightInKg())
                .grossWeightInKg(productLong.getGrossWeightInKg())
                .build();
    }

    private List<String> unwrapUrls(List<Image> images) {
        return images.stream()
                .map(Image::getUrl)
                .toList();
    }
}
