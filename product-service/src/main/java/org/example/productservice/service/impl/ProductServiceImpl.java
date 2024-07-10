package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.elasticsearch.ElasticSearchService;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.exception.ImageNotFoundException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.ProductDetailsMapper;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.mapper.RequestProductMapper;
import org.example.productservice.model.*;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ImageRepository;
import org.example.productservice.repository.ProductDetailsRepository;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.CategoryService;
import org.example.productservice.service.ProductService;
import org.example.productservice.util.PaginationUtils;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryService categoryService;

    private final ElasticSearchService elasticSearchService;

    private final ProductRepository productRepository;
    private final ProductDetailsRepository detailsRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;

    private final ProductDetailsMapper detailsMapper;
    private final ProductMapper productMapper;
    private final RequestProductMapper requestProductMapper;

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable,
                                               String category,
                                               BigDecimal minPrice,
                                               BigDecimal maxPrice) {

        ProductService.validateSortParameters(pageable.getSort());
        Predicate<Product> filter = createProductsFilter(minPrice, maxPrice);

        if (category == null) {
            List<Product> products = productRepository.findAll(pageable.getSort());
            Function<Product, ProductDto> mapper = productMapper::toDto;

            return PaginationUtils.collectionToPageWithFilter(products, pageable, mapper, filter);
        }

        return categoryService.getProductsByCategory(category, pageable, filter);
    }

    @Override
    public List<ProductDto> search(String keyword) {
        List<Integer> matchedProductsId = elasticSearchService.search(keyword);
        return productRepository.findAllById(matchedProductsId).stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> reindex() {
        List<Product> allProducts = productRepository.findAll();
        allProducts.forEach(elasticSearchService::update);

        return allProducts.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductDetailsDto getById(int id) {
        ProductDetails productDetails = detailsRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        return detailsMapper.toDto(productDetails);
    }

    @Override
    public ProductDetailsDto deleteById(int id) {
        ProductDetails productToBeDeleted = detailsRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        detailsRepository.delete(productToBeDeleted);
        return detailsMapper.toDto(productToBeDeleted);
    }

    @Override
    public ProductDetailsDto updateProduct(int id, RequestProductDto updatedProduct) {

        Optional<ProductDetails> optionalProductDetails = detailsRepository.findById(id);

        if (optionalProductDetails.isEmpty()) {
            return createProduct(updatedProduct);
        }

        ProductDetails productToBeUpdated = optionalProductDetails.get();
        updateProduct(productToBeUpdated, updatedProduct);
        productToBeUpdated = detailsRepository.save(productToBeUpdated);
        elasticSearchService.update(productToBeUpdated.getProduct());

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

        createdProduct = detailsRepository.save(createdProduct);
        elasticSearchService.save(createdProduct.getProduct());

        return detailsMapper.toDto(createdProduct);
    }

    private void updateProduct(ProductDetails toBeUpdated, RequestProductDto updated) {

        toBeUpdated.setCountryManufacturer(updated.countryManufacturer());
        toBeUpdated.setLengthInMeters(updated.lengthInMeters());
        toBeUpdated.setWidthInMeters(updated.widthInMeters());
        toBeUpdated.setHeightInMeters(updated.heightInMeters());
        toBeUpdated.setGrossWeightInKg(updated.grossWeightInKg());

        Product innerProduct = toBeUpdated.getProduct();
        innerProduct.setName(updated.name());
        innerProduct.setNetWeightInKg(updated.netWeightInKg());
        innerProduct.setDescription(updated.description());
        innerProduct.setBrand(updated.brand());

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

    private List<Category> fetchCategoriesByIds(Set<Integer> ids) {

        List<Category> foundCategories = categoryRepository.findAllByIdIn(ids);
        List<Integer> foundIds = foundCategories.stream().map(Category::getId).toList();
        foundIds.forEach(ids::remove);
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

    private Predicate<Product> createProductsFilter(BigDecimal minPrice, BigDecimal maxPrice) {
        return product -> {
            BigDecimal priceAmount = product.getPrice().getAmount();
            return (minPrice == null || priceAmount.compareTo(minPrice) >= 0) &&
                    (maxPrice == null || priceAmount.compareTo(maxPrice) <= 0);
        };
    }
}