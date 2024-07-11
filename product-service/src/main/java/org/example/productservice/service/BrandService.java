package org.example.productservice.service;

import org.example.productservice.model.Brand;

public interface BrandService {

    Brand saveOrGetExistingByName(String name);

    Brand updateBrandName(String updatedName, Brand toBeUpdated);
}