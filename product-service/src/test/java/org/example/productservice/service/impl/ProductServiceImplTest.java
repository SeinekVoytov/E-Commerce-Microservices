package org.example.productservice.service.impl;

import org.example.productservice.dto.*;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.exception.ImageNotFoundException;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.ProductDetailsMapper;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.mapper.RequestProductMapper;
import org.example.productservice.model.*;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ImageRepository;
import org.example.productservice.repository.ProductDetailsRepository;
import org.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

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
                .price(Price.builder()
                        .id(id)
                        .amount(priceAmount)
                        .currency(currency)
                        .build())
                .categories(Collections.emptySet())
                .images(Collections.emptySet())
                .build();

        productDetails = ProductDetails.builder()
                .id(id)
                .lengthInMeters(lengthInMeters)
                .widthInMeters(widthInMeters)
                .heightInMeters(heightInMeters)
                .netWeightInKg(netWeightInKg)
                .grossWeightInKg(grossWeightInKg)
                .product(product)
                .build();

        productDto = new ProductDto(
                id,
                name,
                Collections.emptySet(),
                new PriceDto(priceAmount, currency),
                Collections.emptySet()
        );

        detailsDto = new ProductDetailsDto(
                id,
                name,
                Collections.emptySet(),
                new PriceDto(priceAmount, currency),
                Collections.emptySet(),
                lengthInMeters,
                widthInMeters,
                heightInMeters,
                netWeightInKg,
                grossWeightInKg
        );
    }

    @Test
    void getAllShortProduct_ShouldThrowInvalidQueryParameterException_WhenUnknownOrderSpecified() {

        assertThrows(
                InvalidQueryParameterException.class,
                () -> service.getAllShortProduct(PageRequest.of(
                        1, 10, Sort.by(Sort.Order.asc("category")))
                )
        );

        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllShortProduct_ShouldReturnListWithOneElement_WhenOneProductExists() {

        var pageNumber = 0;
        var pageSize = 10;
        var totalElements = 1;

        var pageable = PageRequest.of(pageNumber, pageSize);
        var resultPage = new PageImpl<>(List.of(product), pageable, totalElements);

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(resultPage);

        when(productMapper.toDto(any(Product.class)))
                .thenReturn(productDto);

        var actual = service.getAllShortProduct(pageable);

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
                Collections.emptySet(),
                new BigDecimal("123.456"),
                newCurrency,
                Collections.emptySet(),
                newLength,
                1.0,
                1.0,
                1.0,
                newGrossWeight
        );

        when(categoryRepository.findAllByIdIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

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
                .thenReturn(Collections.emptySet());

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

        Category categoryToBeAdded = new Category(10, null, Collections.emptySet(), Collections.emptySet(), "category");
        when(categoryRepository.findAllByIdIn(any(Set.class)))
                .thenReturn(Set.of(categoryToBeAdded));

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

        when(categoryRepository.findAllByIdIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

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

        when(categoryRepository.findAllByIdIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

        when(imageRepository.findAllByUrlIn(any(Set.class)))
                .thenReturn(Collections.emptySet());

        when(detailsMapper.toDto(any(ProductDetails.class)))
                .thenReturn(detailsDto);
    }

    private RequestProductDto buildRequestData() {
        return new RequestProductDto(
                product.getName(),
                new HashSet<>(),
                product.getPrice().getAmount(),
                product.getPrice().getCurrency(),
                new HashSet<>(),
                productDetails.getLengthInMeters(),
                productDetails.getWidthInMeters(),
                productDetails.getHeightInMeters(),
                productDetails.getNetWeightInKg(),
                productDetails.getGrossWeightInKg()
        );
    }
}