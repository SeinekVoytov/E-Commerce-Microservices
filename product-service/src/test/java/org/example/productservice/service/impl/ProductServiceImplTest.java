package org.example.productservice.service.impl;

import org.example.productservice.dto.PriceDto;
import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.elasticsearch.service.impl.ProductSearchService;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.exception.ImageNotFoundException;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.ProductDetailsMapper;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.mapper.RequestProductMapper;
import org.example.productservice.model.Brand;
import org.example.productservice.model.Category;
import org.example.productservice.model.CountryManufacturer;
import org.example.productservice.model.Image;
import org.example.productservice.model.Price;
import org.example.productservice.model.Product;
import org.example.productservice.model.ProductDetails;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ImageRepository;
import org.example.productservice.repository.ProductDetailsRepository;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.BrandService;
import org.example.productservice.service.CountryManufacturerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private BrandService brandService;

    @Mock
    private CountryManufacturerService countryManufacturerService;

    @Mock
    private ProductDetailsRepository detailsRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ProductDetailsMapper detailsMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private RequestProductMapper requestProductMapper;

    @Mock
    private ProductSearchService productSearchService;

    @InjectMocks
    private ProductServiceImpl service;

    private ProductDetails productDetails;
    private ProductDetailsDto detailsDto;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {

        final int id = 1;
        final String name = "name";
        final String description = "description";
        final String brand = "brand";
        final String countryManufacturer = "country";
        final BigDecimal priceAmount = new BigDecimal("123.456");
        final Currency currency = Currency.getInstance("USD");
        final Double lengthInMeters = 1.0;
        final Double widthInMeters = 1.0;
        final Double heightInMeters = 1.0;
        final Double netWeightInKg = 1.0;
        final Double grossWeightInKg = 1.0;

        product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .netWeightInKg(netWeightInKg)
                .countryManufacturer(CountryManufacturer.builder()
                        .id(id)
                        .name(countryManufacturer)
                        .products(Collections.emptySet())
                        .build())
                .price(Price.builder()
                        .id(id)
                        .amount(priceAmount)
                        .currency(currency)
                        .build())
                .brand(Brand.builder()
                        .id(id)
                        .name(brand)
                        .products(Collections.emptySet())
                        .build())
                .categories(Collections.emptyList())
                .images(Collections.emptySet())
                .build();

        productDetails = ProductDetails.builder()
                .id(id)
                .lengthInMeters(lengthInMeters)
                .widthInMeters(widthInMeters)
                .heightInMeters(heightInMeters)
                .grossWeightInKg(grossWeightInKg)
                .product(product)
                .build();

        productDto = new ProductDto(
                id,
                name,
                netWeightInKg,
                description,
                brand,
                countryManufacturer,
                Collections.emptySet(),
                new PriceDto(priceAmount, currency),
                Collections.emptyList()
        );

        detailsDto = new ProductDetailsDto(
                id,
                name,
                description,
                brand,
                countryManufacturer,
                Collections.emptySet(),
                new PriceDto(priceAmount, currency),
                Collections.emptyList(),
                lengthInMeters,
                widthInMeters,
                heightInMeters,
                netWeightInKg,
                grossWeightInKg
        );
    }

    @Test
    void getAllProducts_ShouldThrowInvalidQueryParameterException_WhenUnknownOrderSpecified() {

        assertThrows(
                InvalidQueryParameterException.class,
                () -> service.getAllProducts(PageRequest.of(
                        1, 10, Sort.by(Sort.Order.asc("category"))), null, null, null, null, null
                )
        );

        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllProducts_ShouldReturnListWithOneElement_WhenOneProductExists() {

        var pageNumber = 0;
        var pageSize = 10;
        var totalElements = 1;

        var pageable = PageRequest.of(pageNumber, pageSize);

        when(productRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(product));

        when(productMapper.toDto(any(Product.class)))
                .thenReturn(productDto);

        var actual = service.getAllProducts(pageable, null, null, null, null, null);

        var expectedContent = List.of(productDto);

        assertAll(
                () -> assertEquals(expectedContent, actual.getContent()),
                () -> assertEquals(pageSize, actual.getSize()),
                () -> assertEquals(pageNumber, actual.getNumber()),
                () -> assertEquals(totalElements, actual.getTotalElements())
        );
    }

    @Test
    void getById_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        var testingId = productDetails.getId();
        when(detailsRepository.findById(testingId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.getById(testingId)
        );
    }

    @Test
    void getById_ShouldReturnFoundProductConvertedToDto_WhenExistentIdSpecified() {

        var testingId = productDetails.getId();
        when(detailsRepository.findById(testingId))
                .thenReturn(Optional.of(productDetails));

        when(detailsMapper.toDto(productDetails))
                .thenReturn(detailsDto);

        var expectedProductDetailsDto = detailsDto;
        var actual = service.getById(testingId);

        assertEquals(expectedProductDetailsDto, actual);
    }

    @Test
    void deleteById_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        var testingId = productDetails.getId();
        when(detailsRepository.findById(testingId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.deleteById(testingId)
        );

        verify(detailsRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteById_ShouldCallDeleteRepositoryMethod_WhenExistentIdSpecified() {

        var testingId = productDetails.getId();
        when(detailsRepository.findById(testingId))
                .thenReturn(Optional.of(productDetails));

        when(detailsMapper.toDto(productDetails))
                .thenReturn(detailsDto);

        var expected = detailsDto;
        var actual = service.deleteById(testingId);

        assertEquals(expected, actual);
        verify(detailsRepository, times(1)).delete(productDetails);
    }

    @Test
    void updateProduct_ShouldCreateNewProduct_WhenNonExistentIdSpecified() {

        var testingId = productDetails.getId();
        when(detailsRepository.findById(testingId))
                .thenReturn(Optional.empty());

        setUpMocksForCreateProductWithEmptyImagesAndCategories();
        var requestData = buildRequestData();

        productDetails.getProduct().setImages(null);
        productDetails.getProduct().setCategories(null);

        when(requestProductMapper.toEntity(requestData))
                .thenReturn(productDetails);

        var actual = service.updateProduct(testingId, requestData);

        assertAll(
                () -> assertNotNull(productDetails.getProduct().getImages()),
                () -> assertTrue(productDetails.getProduct().getImages().isEmpty()),
                () -> assertNotNull(productDetails.getProduct().getCategories()),
                () -> assertTrue(productDetails.getProduct().getCategories().isEmpty()),
                () -> assertEquals(detailsDto, actual)
        );

        verify(detailsRepository, times(1)).save(any(ProductDetails.class));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductConvertedToDto_WhenExistentIdSpecified() {

        var testingId = productDetails.getId();
        when(detailsRepository.findById(testingId))
                .thenReturn(Optional.of(productDetails));

        final var newName = "updatedName";
        final var newCurrency = Currency.getInstance("EUR");
        final var newLength = 2.0;
        final var newGrossWeight = 2.0;

        var requestData = new RequestProductDto(
                newName,
                "description",
                "brand",
                Collections.emptySet(),
                new BigDecimal("123.456"),
                newCurrency,
                "country",
                Collections.emptySet(),
                newLength,
                1.0,
                1.0,
                1.0,
                newGrossWeight,
                100
        );

        when(categoryRepository.findAllByIdIn(any(Collection.class)))
                .thenReturn(Collections.emptyList());

        when(imageRepository.findAllByUrlIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

        doAnswer(invocationOnMock -> invocationOnMock.<ProductDetails>getArgument(0))
                .when(detailsRepository).save(any(ProductDetails.class));

        when(detailsMapper.toDto(productDetails))
                .thenReturn(detailsDto);

        service.updateProduct(testingId, requestData);

        assertAll(
                () -> assertEquals(newName, productDetails.getProduct().getName()),
                () -> assertEquals(newCurrency, productDetails.getProduct().getPrice().getCurrency()),
                () -> assertEquals(newLength, productDetails.getLengthInMeters()),
                () -> assertEquals(newGrossWeight, productDetails.getGrossWeightInKg())
        );
    }

    @Test
    void createProduct_ShouldReturnCreatedProductConvertedToDto_WhenNewProductDataIsSpecified() {

        setUpMocksForCreateProductWithEmptyImagesAndCategories();

        productDetails.getProduct().setImages(null);
        productDetails.getProduct().setCategories(null);

        var requestData = buildRequestData();

        when(requestProductMapper.toEntity(requestData))
                .thenReturn(productDetails);

        var actual = service.createProduct(requestData);

        assertAll(
                () -> assertNotNull(productDetails.getProduct().getImages()),
                () -> assertTrue(productDetails.getProduct().getImages().isEmpty()),
                () -> assertNotNull(productDetails.getProduct().getCategories()),
                () -> assertTrue(productDetails.getProduct().getCategories().isEmpty()),
                () -> assertEquals(detailsDto, actual)
        );

        verify(detailsRepository, times(1)).save(any(ProductDetails.class));
    }

    @Test
    void createProduct_ShouldThrowCategoryNotFoundException_WhenCategoryNotFoundById() {

        var requestData = buildRequestData();
        requestData.categoryIds().add(1);

        when(requestProductMapper.toEntity(requestData))
                .thenReturn(productDetails);

        when(categoryRepository.findAllByIdIn(requestData.categoryIds()))
                .thenReturn(Collections.emptyList());

        assertThrows(
                CategoryNotFoundException.class,
                () -> service.createProduct(requestData)
        );

        verify(detailsRepository, never()).save(any(ProductDetails.class));
    }

    @Test
    void createProduct_ShouldCreateNewProductWithOneCategory_WhenExistentCategoryIdSpecified() {

        doAnswer(invocationOnMock -> invocationOnMock.<ProductDetails>getArgument(0))
                .when(detailsRepository).save(any(ProductDetails.class));

        Category categoryToBeAdded = new Category(10, null, Collections.emptySet(), Collections.emptyList(), "category");
        when(categoryRepository.findAllByIdIn(any(Collection.class)))
                .thenReturn(List.of(categoryToBeAdded));

        when(imageRepository.findAllByUrlIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

        var requestData = buildRequestData();

        productDetails.getProduct().setImages(null);
        productDetails.getProduct().setCategories(null);

        when(requestProductMapper.toEntity(requestData))
                .thenReturn(productDetails);

        when(detailsMapper.toDto(any(ProductDetails.class)))
                .thenReturn(detailsDto);

        var actual = service.createProduct(requestData);

        assertAll(
                () -> assertNotNull(productDetails.getProduct().getImages()),
                () -> assertTrue(productDetails.getProduct().getImages().isEmpty()),
                () -> assertNotNull(productDetails.getProduct().getCategories()),
                () -> assertTrue(productDetails.getProduct().getCategories().contains(categoryToBeAdded)),
                () -> assertEquals(detailsDto, actual)
        );
    }

    @Test
    void createProduct_ShouldThrowImageNotFoundException_WhenImageNotFoundByUrl() {

        var requestData = buildRequestData();
        requestData.images().add("someUrl");

        when(requestProductMapper.toEntity(requestData))
                .thenReturn(productDetails);

        when(imageRepository.findAllByUrlIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

        assertThrows(
                ImageNotFoundException.class,
                () -> service.createProduct(requestData)
        );

        verify(detailsRepository, never()).save(any(ProductDetails.class));
    }

    @Test
    void createProduct_ShouldCreateNewProductWithOneImage_WhenExistentImageUrlSpecified() {

        doAnswer(invocationOnMock -> invocationOnMock.<ProductDetails>getArgument(0))
                .when(detailsRepository).save(any(ProductDetails.class));

        when(categoryRepository.findAllByIdIn(any(Collection.class)))
                .thenReturn(Collections.emptyList());

        Image imageToBeAdded = new Image(10, "someUrl");
        when(imageRepository.findAllByUrlIn(any(Set.class)))
                .thenReturn(Set.of(imageToBeAdded));

        var requestData = buildRequestData();

        productDetails.getProduct().setImages(null);
        productDetails.getProduct().setCategories(null);

        when(requestProductMapper.toEntity(requestData))
                .thenReturn(productDetails);

        when(detailsMapper.toDto(any(ProductDetails.class)))
                .thenReturn(detailsDto);

        var actual = service.createProduct(requestData);

        assertAll(
                () -> assertNotNull(productDetails.getProduct().getImages()),
                () -> assertTrue(productDetails.getProduct().getImages().contains(imageToBeAdded)),
                () -> assertNotNull(productDetails.getProduct().getCategories()),
                () -> assertTrue(productDetails.getProduct().getCategories().isEmpty()),
                () -> assertEquals(detailsDto, actual)
        );
    }

    private void setUpMocksForCreateProductWithEmptyImagesAndCategories() {

        doAnswer(invocationOnMock -> invocationOnMock.<ProductDetails>getArgument(0))
                .when(detailsRepository).save(any(ProductDetails.class));

        when(categoryRepository.findAllByIdIn(any(Collection.class)))
                .thenReturn(Collections.emptyList());

        when(imageRepository.findAllByUrlIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

        when(detailsMapper.toDto(any(ProductDetails.class)))
                .thenReturn(detailsDto);
    }

    private RequestProductDto buildRequestData() {
        return new RequestProductDto(
                product.getName(),
                product.getDescription(),
                product.getBrand().getName(),
                new HashSet<>(),
                product.getPrice().getAmount(),
                product.getPrice().getCurrency(),
                product.getCountryManufacturer().getName(),
                new HashSet<>(),
                productDetails.getLengthInMeters(),
                productDetails.getWidthInMeters(),
                productDetails.getHeightInMeters(),
                product.getNetWeightInKg(),
                productDetails.getGrossWeightInKg(),
                100
        );
    }
}