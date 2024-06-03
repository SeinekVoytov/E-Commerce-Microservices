package org.example.productservice.service.impl;

import lombok.AllArgsConstructor;
import org.example.productservice.dto.CreateProductDto;
import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductLongDto;
import org.example.productservice.dto.ProductShortDto;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.CategoryMapper;
import org.example.productservice.mapper.PriceMapper;
import org.example.productservice.mapper.ProductLongMapper;
import org.example.productservice.mapper.ProductShortMapper;
import org.example.productservice.model.*;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ProductLongRepository;
import org.example.productservice.repository.ProductShortRepository;
import org.example.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Set<String> AVAILABLE_SORT_PARAMETERS = Set.of("price.amount");
    private static final Set<String> AVAILABLE_ORDER = Set.of("asc", "desc");

    private final ProductShortRepository shortRepo;
    private final ProductLongRepository longRepo;
    private final CategoryRepository categoryRepo;

    private final ProductLongMapper longMapper;
    private final ProductShortMapper shortMapper;
    private final PriceMapper priceMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageProductShortDto getAllShortProduct(int pageNo, int pageSize, String order) {

        String[] sortingParams = order.trim().split(":");

        Pageable pageable = PageRequest.of(pageNo, pageSize, createSort(sortingParams[1], sortingParams[0]));
        Page<ProductShort> shortProducts = shortRepo.findAll(pageable);

        List<ProductShortDto> pageContent = shortProducts.getContent().stream()
                .map(shortMapper::toDto)
                .toList();

        return new PageProductShortDto(
                pageContent,
                shortProducts.getNumber(),
                shortProducts.getSize(),
                shortProducts.getTotalElements(),
                shortProducts.getTotalPages()
        );
    }

    @Override
    public ProductLongDto getById(int id) {
        ProductLong productLong = longRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product could not be found"));
        return longMapper.toDto(productLong);
    }

    @Override
    public ProductLongDto deleteById(int id) {
        ProductLong productToBeDeleted = longRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product could not be deleted"));
        longRepo.delete(productToBeDeleted);
        return longMapper.toDto(productToBeDeleted);
    }

    @Override
    public ProductLongDto updateProduct(int id, ProductLongDto updatedProduct) {
        ProductLong productToBeUpdated = longRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product could not be updated"));
        updateProduct(productToBeUpdated, updatedProduct);
        productToBeUpdated = longRepo.save(productToBeUpdated);
        return longMapper.toDto(productToBeUpdated);
    }

    @Override
    public ProductLongDto createProduct(CreateProductDto newProductData) {

        ProductLong createdProduct = new ProductLong();
        createdProduct.setLengthInM(newProductData.lengthInM());
        createdProduct.setWidthInM(newProductData.widthInM());
        createdProduct.setHeightInM(newProductData.heightInM());
        createdProduct.setNetWeightInKg(newProductData.netWeightInKg());
        createdProduct.setGrossWeightInKg(newProductData.grossWeightInKg());

        ProductShort productShort = new ProductShort();
        productShort.setName(newProductData.name());
        Iterable<Category> selectedCategories = categoryRepo.findAllById(newProductData.categoryIds());

        List<Category> categoriesToBeSaved = new ArrayList<>();
        for (Category selectedCategory : selectedCategories) {
            categoriesToBeSaved.add(selectedCategory);
        }

        productShort.setCategories(categoriesToBeSaved);

        Price priceToBeSaved = new Price();
        priceToBeSaved.setAmount(newProductData.priceAmount());
        priceToBeSaved.setCurrency(newProductData.priceCurrency());

        productShort.setPrice(priceToBeSaved);
        productShort.setImages(new ArrayList<>());
        createdProduct.setProductShort(productShort);

        createdProduct = longRepo.save(createdProduct);

        return longMapper.toDto(createdProduct);
    }

    private void updateProduct(ProductLong toBeUpdated, ProductLongDto updated) {

        if (updated.lengthInM() != null) {
            toBeUpdated.setLengthInM(updated.lengthInM());
        }

        if (updated.widthInM() != null) {
            toBeUpdated.setWidthInM(updated.widthInM());
        }

        if (updated.heightInM() != null) {
            toBeUpdated.setHeightInM(updated.heightInM());
        }

        if (updated.netWeightInKg() != null) {
            toBeUpdated.setNetWeightInKg(updated.netWeightInKg());
        }

        if (updated.grossWeightInKg() != null) {
            toBeUpdated.setGrossWeightInKg(updated.grossWeightInKg());
        }

        ProductShort insideProduct = toBeUpdated.getProductShort();

        if (updated.name() != null) {
            insideProduct.setName(updated.name());
        }

        if (updated.price() != null) {
            insideProduct.setPrice(priceMapper.toEntity(updated.price()));
        }

        if (updated.categories() != null) {
            insideProduct.setCategories(categoryMapper.toEntitiesList(updated.categories()));
        }
    }

    private Sort createSort(String order, String sortParam) {

        if (!AVAILABLE_ORDER.contains(order) || !AVAILABLE_SORT_PARAMETERS.contains(sortParam)) {
            throw new InvalidQueryParameterException("Invalid query parameter <order> value");
        }

        if (order.equals("desc")) {
            return Sort.by(sortParam).descending();
        }

        return Sort.by(sortParam).ascending();
    }
}
