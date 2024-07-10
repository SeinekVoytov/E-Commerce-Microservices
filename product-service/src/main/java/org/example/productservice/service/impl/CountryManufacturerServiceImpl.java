package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.model.CountryManufacturer;
import org.example.productservice.repository.CountryManufacturerRepository;
import org.example.productservice.service.CountryManufacturerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryManufacturerServiceImpl implements CountryManufacturerService {

    private final CountryManufacturerRepository countryManufacturerRepository;

    @Override
    public CountryManufacturer saveOrGetExistingByName(String name) {
        return countryManufacturerRepository.findByName(name).orElseGet(
                () -> countryManufacturerRepository.save(CountryManufacturer.builder().name(name).build())
        );
    }

    @Override
    public CountryManufacturer updateCountryManufacturerName(String updatedName, CountryManufacturer toBeUpdated) {

        String toBeUpdatedName = toBeUpdated.getName();
        if (!updatedName.equals(toBeUpdatedName)) {
             return saveOrGetExistingByName(updatedName);
        }

        return toBeUpdated;
    }
}
