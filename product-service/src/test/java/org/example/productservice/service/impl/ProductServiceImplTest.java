package org.example.productservice.service.impl;

import org.example.productservice.dto.*;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.CategoryMapper;
import org.example.productservice.mapper.PriceMapper;
import org.example.productservice.mapper.ProductLongMapper;
import org.example.productservice.mapper.ProductShortMapper;
import org.example.productservice.model.Price;
import org.example.productservice.model.ProductLong;
import org.example.productservice.model.ProductShort;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ProductLongRepository;
import org.example.productservice.repository.ProductShortRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductLongRepository longRepo;

    @Mock
    private ProductShortRepository shortRepo;

    @Mock
    private CategoryRepository categoryRepo;

    @Mock
    private ProductLongMapper longMapper;

    @Mock
    private ProductShortMapper shortMapper;

    @InjectMocks
    private ProductServiceImpl service;

    private ProductLong productLong;
    private ProductLongDto longDto;

    private ProductShort productShort;
    private ProductShortDto shortDto;

    @BeforeEach
    void setUp() {

        final int id = 1;
        final String name = "name";
        final Float priceAmount = 10f;
        final String currency = "USD";
        final Float lengthInM = 1.0f;
        final Float widthInM = 1.0f;
        final Float heightInM = 1.0f;
        final Float netWeightInKg = 1.0f;
        final Float grossWeightInKg = 1.0f;

        productShort = ProductShort.builder()
                .id(id)
                .name(name)
                .price(Price.builder()
                        .id(id)
                        .amount(priceAmount)
                        .currency(currency)
                        .build())
                .categories(Collections.emptyList())
                .images(Collections.emptyList())
                .build();

        productLong = ProductLong.builder()
                .id(id)
                .lengthInM(lengthInM)
                .widthInM(widthInM)
                .heightInM(heightInM)
                .netWeightInKg(netWeightInKg)
                .grossWeightInKg(grossWeightInKg)
                .productShort(productShort)
                .build();

        productShort.setProductLong(productLong);

        shortDto = new ProductShortDto(
                id,
                name,
                Collections.emptyList(),
                new PriceDto(priceAmount, currency),
                Collections.emptyList()
        );

        longDto = new ProductLongDto(
                id,
                name,
                Collections.emptyList(),
                new PriceDto(priceAmount, currency),
                Collections.emptyList(),
                lengthInM,
                widthInM,
                heightInM,
                netWeightInKg,
                grossWeightInKg
        );
    }

    @Test
    void getAllShortProduct_ShouldThrowInvalidQueryParameterException_WhenUnknownOrderSpecified() {

        assertThrows(
                InvalidQueryParameterException.class,
                () -> service.getAllShortProduct(1, 1, "categories:asc")
        );

        verify(shortRepo, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllShortProduct_ShouldReturnListWithOneElement_WhenOneProductExists() {

        final int pageNumber = 1;
        final int pageSize = 10;
        final long totalElements = 1;
        final int totalPages = 1;

        Page<ProductShort> resultPage = mock(Page.class);
        when(resultPage.getContent()).thenReturn(List.of(productShort));
        when(resultPage.getNumber()).thenReturn(pageNumber);
        when(resultPage.getSize()).thenReturn(pageSize);
        when(resultPage.getTotalElements()).thenReturn(totalElements);
        when(resultPage.getTotalPages()).thenReturn(totalPages);

        when(shortRepo.findAll(any(Pageable.class))).thenReturn(resultPage);
        when(shortMapper.toDto(any(ProductShort.class))).thenReturn(shortDto);

        PageProductShortDto actual = service.getAllShortProduct(pageNumber, pageSize, "price.amount:asc");

        PageProductShortDto expected = new PageProductShortDto(
                List.of(shortDto), pageNumber, pageSize, totalElements, totalPages
        );

        assertEquals(expected, actual);
    }

    @Test
    void getById_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        int testingId = productLong.getId();
        when(longRepo.findById(testingId)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.getById(testingId)
        );
    }

    @Test
    void getById_ShouldReturnFoundProductConvertedToDto_WhenExistentIdSpecified() {

        int testingId = productLong.getId();
        when(longRepo.findById(testingId)).thenReturn(Optional.of(productLong));
        when(longMapper.toDto(productLong)).thenReturn(longDto);

        ProductLongDto expectedProductLongDto = longDto;
        ProductLongDto actual = service.getById(testingId);

        assertEquals(expectedProductLongDto, actual);
    }

    @Test
    void deleteById_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        int testingId = productLong.getId();
        when(longRepo.findById(testingId)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.deleteById(testingId)
        );

        verify(longRepo, never()).deleteById(anyInt());
    }

    @Test
    void deleteById_ShouldCallDeleteRepositoryMethod_WhenExistentIdSpecified() {

        int testingId = productLong.getId();
        when(longRepo.findById(testingId)).thenReturn(Optional.of(productLong));
        when(longMapper.toDto(productLong)).thenReturn(longDto);

        ProductLongDto expected = longDto;
        ProductLongDto actual = service.deleteById(testingId);

        assertEquals(expected, actual);
        verify(longRepo, times(1)).delete(productLong);
    }

    @Test
    void updateProduct_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        int testingId = productLong.getId();
        when(longRepo.findById(testingId)).thenReturn(Optional.empty());
        ProductLongDto stub = mock(ProductLongDto.class);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.updateProduct(testingId, stub)
        );

        verify(longRepo, never()).save(any());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductConvertedToDto_WhenExistentIdSpecified() {

        int testingId = productLong.getId();
        when(longRepo.findById(testingId)).thenReturn(Optional.of(productLong));

        when(longRepo.save(any(ProductLong.class))).thenAnswer(
                invocationOnMock -> invocationOnMock.getArgument(0)
        );

        when(longMapper.toDto(productLong)).thenReturn(longDto);

        String updatedName = "updated";
        Float updatedLengthInM = 2.0f;
        productLong.getProductShort().setName(updatedName);
        productLong.setLengthInM(updatedLengthInM);

        ProductLongDto updated = ProductLongDto.builder()
                .name(updatedName)
                .lengthInM(updatedLengthInM)
                .build();

        ProductLongDto actual = service.updateProduct(testingId, updated);

        assertEquals(longDto, actual);
    }

    @Test
    void updateProduct_ShouldReturnSameProduct_WhenEmptyUpdateDataSpecified() {

        int testingId = productLong.getId();
        when(longRepo.findById(testingId)).thenReturn(Optional.of(productLong));

        when(longRepo.save(any())).thenAnswer(
                invocationOnMock -> invocationOnMock.getArgument(0)
        );

        when(longMapper.toDto(productLong)).thenReturn(longDto);

        ProductLongDto emptyUpdateData = ProductLongDto.builder().build();

        ProductLongDto actual = service.updateProduct(testingId, emptyUpdateData);

        assertEquals(longDto, actual);
        verify(longRepo, times(1)).findById(testingId);
        verify(longRepo, times(1)).save(any());
    }

    @Test
    void createProduct_ShouldReturnCreatedProductConvertedToDto_WhenNewProductDataIsSpecified() {

        doAnswer(invocationOnMock -> {
            ProductLong param = invocationOnMock.getArgument(0);
            param.setId(1);
            param.getProductShort().getPrice().setId(1);
            return param;
        }).when(longRepo).save(any(ProductLong.class));

        when(categoryRepo.findAllById(any(Iterable.class)))
                .thenReturn(Collections.emptyList());

        when(longMapper.toDto(any(ProductLong.class))).thenReturn(longDto);

        CreateProductDto newProductData = new CreateProductDto(
                productShort.getName(),
                productShort.getPrice().getAmount(),
                productShort.getPrice().getCurrency(),
                Collections.emptyList(),
                productLong.getLengthInM(),
                productLong.getWidthInM(),
                productLong.getHeightInM(),
                productLong.getNetWeightInKg(),
                productLong.getGrossWeightInKg()
        );

        ProductLongDto actual = service.createProduct(newProductData);

        assertEquals(longDto, actual);
        verify(longRepo, times(1)).save(any(ProductLong.class));
    }
}