package org.example.productservice.service.impl;

import org.example.productservice.model.CountryManufacturer;
import org.example.productservice.repository.CountryManufacturerRepository;
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
class CountryManufacturerServiceImplTest {

    @Mock
    private CountryManufacturerRepository countryManufacturerRepository;

    @InjectMocks
    private CountryManufacturerServiceImpl countryManufacturerService;

    @Test
    void saveOrGetExistingByName_ShouldReturnExistingCountry_WhenCountryIsFoundByName() {

        var name = "country";
        var existingCountry = new CountryManufacturer(1, name, Collections.emptySet());
        when(countryManufacturerRepository.findByName(name)).thenReturn(Optional.of(existingCountry));

        var result = countryManufacturerService.saveOrGetExistingByName(name);

        assertEquals(existingCountry, result);
        verify(countryManufacturerRepository, never()).save(any(CountryManufacturer.class));
    }

    @Test
    void saveOrGetExistingByName_ShouldReturnCreatedCountry_WhenCountryIsNotFoundByName() {

        var name = "country";
        when(countryManufacturerRepository.findByName(name)).thenReturn(Optional.empty());

        var countryReturnedAfterSaving = new CountryManufacturer(1, name, Collections.emptySet());
        when(countryManufacturerRepository.save(any(CountryManufacturer.class)))
                .thenReturn(countryReturnedAfterSaving);

        var result = countryManufacturerService.saveOrGetExistingByName(name);
        assertEquals(countryReturnedAfterSaving, result);
    }

    @Test
    void updateCountryName_ShouldReturnSameCountry_WhenUpdatedAndToBeUpdatedNamesAreEqual() {

        var updatedName = "country";
        var toBeUpdated = new CountryManufacturer(1, updatedName, Collections.emptySet());

        var result = countryManufacturerService.updateCountryManufacturerName(updatedName, toBeUpdated);

        assertEquals(toBeUpdated, result);
        verify(countryManufacturerRepository, never()).findByName(updatedName);
        verify(countryManufacturerRepository, never()).save(any(CountryManufacturer.class));
    }

    @Test
    void updateCountryName_ShouldReturnExistingCountry_WhenCountryWithUpdatedNameAlreadyExists() {

        var name = "country";
        var countryTeBeUpdated = new CountryManufacturer(1, "oldCountry", Collections.emptySet());
        var existingCountry = new CountryManufacturer(2, name, Collections.emptySet());

        when(countryManufacturerRepository.findByName(name)).thenReturn(Optional.of(existingCountry));

        var result = countryManufacturerService.updateCountryManufacturerName(name, countryTeBeUpdated);

        assertEquals(existingCountry, result);
        verify(countryManufacturerRepository, never()).save(any(CountryManufacturer.class));
    }

    @Test
    void updateCountryName_ShouldReturnNewCountry_WhenCountryWithUpdatedNameDoNotExist() {

        var name = "country";
        var countryTeBeUpdated = new CountryManufacturer(1, "oldCountry", Collections.emptySet());
        var createdCountry = new CountryManufacturer(2, name, Collections.emptySet());

        when(countryManufacturerRepository.findByName(name)).thenReturn(Optional.empty());
        when(countryManufacturerRepository.save(any(CountryManufacturer.class))).thenReturn(createdCountry);

        var result = countryManufacturerService.updateCountryManufacturerName(name, countryTeBeUpdated);

        assertEquals(createdCountry, result);
        verify(countryManufacturerRepository).findByName(name);
        verify(countryManufacturerRepository).save(any(CountryManufacturer.class));
    }
}