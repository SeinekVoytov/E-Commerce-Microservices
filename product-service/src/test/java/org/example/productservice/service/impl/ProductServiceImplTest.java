package org.example.productservice.service.impl;

import org.example.productservice.dto.CreateProductDto;
import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductLongDto;
import org.example.productservice.dto.ProductShortDto;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.model.Image;
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
    private ProductLongRepository longRepository;

    @Mock
    private ProductShortRepository shortRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl service;

    private ProductLong productLong;
    private ProductShort productShort;

    @BeforeEach
    void setUp() {

        productShort = ProductShort.builder()
                .id(1)
                .name("product")
                .price(Price.builder()
                        .id(1)
                        .amount(10f)
                        .currency("USD")
                        .build())
                .categories(Collections.emptyList())
                .images(Collections.emptyList())
                .build();

        productLong = ProductLong.builder()
                .id(1)
                .lengthInM(1.0f)
                .widthInM(1.0f)
                .heightInM(1.0f)
                .netWeightInKg(1.0f)
                .grossWeightInKg(1.0f)
                .productShort(productShort)
                .build();

        productShort.setProductLong(productLong);
    }

    @Test
    void getAllShortProduct_ShouldThrowInvalidQueryParameterException_WhenUnknownOrderSpecified() {

        assertThrows(InvalidQueryParameterException.class, () -> service.getAllShortProduct(1, 1, "categories:asc"));

        verify(shortRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllShortProduct_ShouldReturnListWithOneElement_WhenOneProductExists() {

        final int testingPageSize = 10;

        Page<ProductShort> resultPage = mock(Page.class);
        when(resultPage.getContent())
                .thenReturn(List.of(productShort));

        when(resultPage.getNumber())
                .thenReturn(1);

        when(resultPage.getSize())
                .thenReturn(testingPageSize);

        when(resultPage.getTotalElements())
                .thenReturn(1L);

        when(resultPage.getTotalPages())
                .thenReturn(1);

        when(shortRepository.findAll(any(Pageable.class)))
                .thenReturn(resultPage);

        PageProductShortDto actual = service.getAllShortProduct(1, testingPageSize, "price.amount:asc");

        ProductShortDto shortDto = ProductShortDto.builder()
                .id(productShort.getId())
                .name(productShort.getName())
                .images(unwrapImages(productShort.getImages()))
                .price(productShort.getPrice())
                .categories(productShort.getCategories())
                .build();

        PageProductShortDto expected = PageProductShortDto.builder()
                .content(List.of(shortDto))
                .pageNo(1)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void getById_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        when(longRepository.findById(productLong.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.getById(productLong.getId()));
    }

    @Test
    void getById_ShouldReturnFoundProductConvertedToDto_WhenExistentIdSpecified() {

        when(longRepository.findById(productLong.getId()))
                .thenReturn(Optional.of(productLong));

        ProductLongDto expectedProductLongDto = mapToLongDto(productLong);

        ProductLongDto actual = service.getById(productLong.getId());

        assertEquals(expectedProductLongDto, actual);
    }

    @Test
    void deleteById_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        when(longRepository.findById(productLong.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.deleteById(productLong.getId()));

        verify(longRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteById_ShouldCallDeleteRepositoryMethod_WhenExistentIdSpecified() {

        when(longRepository.findById(productLong.getId()))
                .thenReturn(Optional.of(productLong));

        doNothing().when(longRepository).delete(productLong);

        service.deleteById(productLong.getId());

        verify(longRepository, times(1)).delete(productLong);
    }

    @Test
    void updateProduct_ShouldThrowProductNotFoundException_WhenNonExistentIdSpecified() {

        when(longRepository.findById(productLong.getId()))
                .thenReturn(Optional.empty());

        ProductLongDto stub = new ProductLongDto();

        assertThrows(ProductNotFoundException.class, () -> service.updateProduct(productLong.getId(), stub));

        verify(longRepository, never()).save(any());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductConvertedToDto_WhenExistentIdSpecified() {

        when(longRepository.findById(productLong.getId()))
                .thenReturn(Optional.of(productLong));

        when(longRepository.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ProductLongDto updated = ProductLongDto.builder()
                        .name("updated")
                        .price(Price.builder()
                                    .amount(11f)
                                    .build())
                        .lengthInM(2.0f)
                        .build();

        ProductLongDto actual = service.updateProduct(productLong.getId(), updated);

        ProductLongDto expected = ProductLongDto.builder()
                .id(productLong.getId())
                .name(updated.getName())
                .images(unwrapImages(productShort.getImages()))
                .price(Price.builder()
                        .id(productShort.getPrice().getId())
                        .amount(updated.getPrice().getAmount())
                        .currency(productShort.getPrice().getCurrency())
                        .build())
                .categories(productShort.getCategories())
                .lengthInM(updated.getLengthInM())
                .widthInM(productLong.getWidthInM())
                .heightInM(productLong.getHeightInM())
                .netWeightInKg(productLong.getNetWeightInKg())
                .grossWeightInKg(productLong.getGrossWeightInKg())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void updateProduct_ShouldReturnSameProduct_WhenEmptyUpdateDataSpecified() {

        when(longRepository.findById(productLong.getId()))
                .thenReturn(Optional.of(productLong));

        when(longRepository.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ProductLongDto emptyUpdateData = new ProductLongDto();

        ProductLongDto actual = service.updateProduct(productLong.getId(), emptyUpdateData);

        ProductLongDto expected = ProductLongDto.builder()
                .id(productLong.getId())
                .name(productShort.getName())
                .images(unwrapImages(productShort.getImages()))
                .price(productShort.getPrice())
                .categories(productShort.getCategories())
                .lengthInM(productLong.getLengthInM())
                .widthInM(productLong.getWidthInM())
                .heightInM(productLong.getHeightInM())
                .netWeightInKg(productLong.getNetWeightInKg())
                .grossWeightInKg(productLong.getGrossWeightInKg())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void createProduct_ShouldReturnCreatedProductConvertedToDto_WhenNewProductDataIsSpecified() {

        doAnswer(invocationOnMock -> {
            ProductLong param = invocationOnMock.getArgument(0);
            param.setId(2);
            param.getProductShort().getPrice().setId(2);
            return param;
        }).when(longRepository).save(any(ProductLong.class));

        when(categoryRepository.findAllById(any(Iterable.class)))
                .thenReturn(Collections.emptyList());

        CreateProductDto newProductData = CreateProductDto.builder()
                .name(productShort.getName())
                .priceAmount(productShort.getPrice().getAmount())
                .priceCurrency(productShort.getPrice().getCurrency())
                .categoryIds(Collections.emptyList())
                .lengthInM(productLong.getLengthInM())
                .widthInM(productLong.getWidthInM())
                .heightInM(productLong.getHeightInM())
                .netWeightInKg(productLong.getNetWeightInKg())
                .grossWeightInKg(productLong.getGrossWeightInKg())
                .build();

        ProductLongDto actual = service.createProduct(newProductData);
        ProductLongDto expected = mapToLongDto(productLong);
        expected.setId(2);
        expected.getPrice().setId(2);

        assertEquals(expected, actual);
    }

    private List<String> unwrapImages(List<Image> images) {
        return images.stream()
                .map(Image::getUrl)
                .toList();
    }

    private ProductLongDto mapToLongDto(ProductLong productLong) {
        return ProductLongDto.builder()
                .id(productLong.getId())
                .name(productLong.getProductShort().getName())
                .images(unwrapImages(productLong.getProductShort().getImages()))
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