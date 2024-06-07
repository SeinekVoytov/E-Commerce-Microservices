package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.exception.ImageNotFoundException;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.*;
import org.example.productservice.model.*;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ImageRepository;
import org.example.productservice.repository.ProductDetailsRepository;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Set<String> AVAILABLE_SORT_PARAMETERS = Set.of("name", "price.amount");

    private final ProductRepository shortRepository;
    private final ProductDetailsRepository longRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;

    private final ProductDetailsMapper detailsMapper;
    private final ProductMapper productMapper;
    private final RequestProductMapper requestProductMapper;

    @Override
    public Page<ProductDto> getAllShortProduct(Pageable pageable) {
        validateSortParameters(pageable.getSort());
        return shortRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Override
    public ProductDetailsDto getById(int id) {
        ProductDetails productDetails = longRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        return detailsMapper.toDto(productDetails);
    }

    @Override
    public ProductDetailsDto deleteById(int id) {
        ProductDetails productToBeDeleted = longRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        longRepository.delete(productToBeDeleted);
        return detailsMapper.toDto(productToBeDeleted);
    }

    @Override
    public ProductDetailsDto updateProduct(int id, RequestProductDto updatedProduct) {
        Optional<ProductDetails> optionalProductDetails = longRepository.findById(id);

        if (optionalProductDetails.isEmpty()) {
            return createProduct(updatedProduct);
        }

        ProductDetails productToBeUpdated = optionalProductDetails.get();
        updateProduct(productToBeUpdated, updatedProduct);
        productToBeUpdated = longRepository.save(productToBeUpdated);
        return detailsMapper.toDto(productToBeUpdated);
    }

    @Override
    public ProductDetailsDto createProduct(RequestProductDto newProductData) {

        ProductDetails createdProduct = requestProductMapper.toEntity(newProductData);

        createdProduct.getProduct().setCategories(
                fetchCategoriesByIds(newProductData.categoryIds())
        );

        createdProduct.getProduct().setImages(
                fetchImagesByUrls(newProductData.images())
        );

        createdProduct = longRepository.save(createdProduct);
        return detailsMapper.toDto(createdProduct);
    }

    private void updateProduct(ProductDetails toBeUpdated, RequestProductDto updated) {

        toBeUpdated.setLengthInMeters(updated.lengthInMeters());
        toBeUpdated.setWidthInMeters(updated.widthInMeters());
        toBeUpdated.setHeightInMeters(updated.heightInMeters());
        toBeUpdated.setNetWeightInKg(updated.netWeightInKg());
        toBeUpdated.setGrossWeightInKg(updated.grossWeightInKg());

        Product innerProduct = toBeUpdated.getProduct();
        innerProduct.setName(updated.name());

        Price price = innerProduct.getPrice();
        price.setAmount(updated.priceAmount());
        price.setCurrency(updated.priceCurrency());
        innerProduct.setPrice(price);

        innerProduct.setCategories(
                fetchCategoriesByIds(updated.categoryIds())
        );

        innerProduct.setImages(
                fetchImagesByUrls(updated.images())
        );

        toBeUpdated.setProduct(innerProduct);
    }

    private Set<Category> fetchCategoriesByIds(Set<Integer> ids) {

        Set<Category> foundCategories = categoryRepository.findAllByIdIn(ids);
        Set<Integer> foundIds = foundCategories.stream().map(Category::getId).collect(Collectors.toSet());
        ids.removeAll(foundIds);
        for (Integer missedId : ids) {
            throw new CategoryNotFoundException(missedId);
        }

        return foundCategories;
    }

    private Set<Image> fetchImagesByUrls(Set<String> urls) {

        Set<Image> foundImages = imageRepository.findAllByUrlIn(urls);
        Set<String> foundUrls = foundImages.stream().map(Image::getUrl).collect(Collectors.toSet());
        urls.removeAll(foundUrls);
        for (String missedUrl : urls) {
            throw new ImageNotFoundException(missedUrl);
        }

        return foundImages;
    }

    private void validateSortParameters(Sort sort) {
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (!AVAILABLE_SORT_PARAMETERS.contains(property)) {
                throw new InvalidQueryParameterException("sort", property);
            }
        }
    }
}