package org.example.productservice.service.impl;

import org.example.productservice.model.Brand;
import org.example.productservice.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;

    @Test
    void saveOrGetExistingByName_ShouldReturnExistingBrand_WhenBrandIsFoundByName() {

        var name = "brand";
        var existingBrand = new Brand(1, name, Collections.emptySet());
        when(brandRepository.findByName(name)).thenReturn(Optional.of(existingBrand));

        var result = brandService.saveOrGetExistingByName(name);

        assertEquals(existingBrand, result);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void saveOrGetExistingByName_ShouldReturnCreatedBrand_WhenBrandIsNotFoundByName() {

        var name = "brand";
        when(brandRepository.findByName(name)).thenReturn(Optional.empty());

        var brandReturnedAfterSaving = new Brand(1, name, Collections.emptySet());
        when(brandRepository.save(any(Brand.class))).thenReturn(brandReturnedAfterSaving);

        var result = brandService.saveOrGetExistingByName(name);
        assertEquals(brandReturnedAfterSaving, result);
    }

    @Test
    void updateBrandName_ShouldReturnSameBrand_WhenUpdatedAndToBeUpdatedNamesAreEqual() {

        var updatedName = "brand";
        var toBeUpdated = new Brand(1, updatedName, Collections.emptySet());

        var result = brandService.updateBrandName(updatedName, toBeUpdated);

        assertEquals(toBeUpdated, result);
        verify(brandRepository, never()).findByName(updatedName);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void updateBrandName_ShouldReturnExistingBrand_WhenBrandWithUpdatedNameAlreadyExists() {

        var name = "brand";
        var brandTeBeUpdated = new Brand(1, "oldName", Collections.emptySet());
        var existingBrand = new Brand(2, name, Collections.emptySet());

        when(brandRepository.findByName(name)).thenReturn(Optional.of(existingBrand));

        var result = brandService.updateBrandName(name, brandTeBeUpdated);

        assertEquals(existingBrand, result);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void updateBrandName_ShouldReturnNewBrand_WhenBrandWithUpdatedNameDoNotExist() {

        var name = "brand";
        var brandTeBeUpdated = new Brand(1, "oldName", Collections.emptySet());
        var createdBrand = new Brand(2, name, Collections.emptySet());

        when(brandRepository.findByName(name)).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(createdBrand);

        var result = brandService.updateBrandName(name, brandTeBeUpdated);

        assertEquals(createdBrand, result);
        verify(brandRepository).findByName(name);
        verify(brandRepository).save(any(Brand.class));
    }
}