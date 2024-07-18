package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.BulkProductsResponse;
import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.elasticsearch.service.impl.ProductSearchService;
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
import org.example.productservice.service.BrandService;
import org.example.productservice.service.CategoryService;
import org.example.productservice.service.CountryManufacturerService;
import org.example.productservice.service.ProductService;
import org.example.productservice.util.PaginationUtils;
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
    private final CountryManufacturerService countryManufacturerService;
    private final BrandService brandService;

    private final ProductSearchService productSearchService;

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
                                           BigDecimal maxPrice,
                                           String brand,
                                           String country) {

        ProductService.validateSortParameters(pageable.getSort());
        Predicate<Product> filter = createProductsFilter(minPrice, maxPrice, brand, country);

        if (category == null) {
            List<Product> products = productRepository.findAll(pageable.getSort());
            Function<Product, ProductDto> mapper = productMapper::toDto;

            return PaginationUtils.collectionToPageWithFilter(products, pageable, mapper, filter);
        }

        return categoryService.getProductsByCategory(category, pageable, filter);
    }

    @Override
    public List<ProductDto> search(String keyword) {
        List<Integer> matchedProductsId = productSearchService.search(keyword);
        return productRepository.findAllById(matchedProductsId).stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> reindex() {
        List<Product> allProducts = productRepository.findAll().stream()
                .peek(productSearchService::update)
                .toList();

        return allProducts.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductDetailsDto getById(int id) {
        ProductDetails productDetails = detailsRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return detailsMapper.toDto(productDetails);
    }

    @Override
    public ProductDetailsDto deleteById(int id) {
        ProductDetails productToBeDeleted = detailsRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

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
        productSearchService.update(productToBeUpdated.getProduct());

        return detailsMapper.toDto(productToBeUpdated);
    }

    @Override
    public ProductDetailsDto createProduct(RequestProductDto newProductData) {

        ProductDetails createdProduct = requestProductMapper.toEntity(newProductData);

        Product innerProduct = createdProduct.getProduct();
        innerProduct.setCountryManufacturer(
                countryManufacturerService.saveOrGetExistingByName(
                        newProductData.countryManufacturer()
                )
        );

        innerProduct.setBrand(
                brandService.saveOrGetExistingByName(newProductData.brand())
        );

        innerProduct.setCategories(
                fetchCategoriesByIds(newProductData.categoryIds())
        );

        innerProduct.setImages(
                fetchImagesByUrls(newProductData.images())
        );

        createdProduct = detailsRepository.save(createdProduct);
        productSearchService.save(createdProduct.getProduct());

        return detailsMapper.toDto(createdProduct);
    }

    @Override
    public BulkProductsResponse getProductsByIds(List<Integer> ids) {
        List<ProductDetails> foundProducts = detailsRepository.findAllById(ids);

        if (foundProducts.size() != ids.size()) {
            ids.removeAll(
                    foundProducts.stream()
                    .map(ProductDetails::getId)
                    .toList()
            );

            throw new ProductNotFoundException(ids);
        }

        return new BulkProductsResponse(
                foundProducts.stream().map(detailsMapper::toDto).toList()
        );
    }

    private void updateProduct(ProductDetails toBeUpdated, RequestProductDto updated) {

        toBeUpdated.setLengthInMeters(updated.lengthInMeters());
        toBeUpdated.setWidthInMeters(updated.widthInMeters());
        toBeUpdated.setHeightInMeters(updated.heightInMeters());
        toBeUpdated.setGrossWeightInKg(updated.grossWeightInKg());

        Product innerProduct = toBeUpdated.getProduct();
        innerProduct.setName(updated.name());
        innerProduct.setNetWeightInKg(updated.netWeightInKg());
        innerProduct.setDescription(updated.description());

        innerProduct.setCountryManufacturer(
                countryManufacturerService.updateCountryManufacturerName(
                        updated.countryManufacturer(), innerProduct.getCountryManufacturer()
                )
        );

        innerProduct.setBrand(
                brandService.updateBrandName(updated.brand(), innerProduct.getBrand())
        );

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

    private Predicate<Product> createProductsFilter(BigDecimal minPrice, BigDecimal maxPrice,
                                                    String brand, String country) {
        return product -> {

            BigDecimal priceAmount = product.getPrice().getAmount();
            String brandName = product.getBrand().getName();
            String countryName = product.getCountryManufacturer().getName();

            return (minPrice == null || priceAmount.compareTo(minPrice) >= 0) &&
                    (maxPrice == null || priceAmount.compareTo(maxPrice) <= 0) &&
                    (brand == null || brandName.equals(brand)) &&
                    (country == null || countryName.equals(country));
        };
    }
}