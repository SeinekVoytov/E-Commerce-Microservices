package org.example.productservice.service;

import org.example.productservice.model.CountryManufacturer;

public interface CountryManufacturerService {

    CountryManufacturer saveOrGetExistingByName(String name);

    CountryManufacturer updateCountryManufacturerName(String updatedName,
                                                      CountryManufacturer toBeUpdated);
}
