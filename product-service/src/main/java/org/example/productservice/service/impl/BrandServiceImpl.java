package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.model.Brand;
import org.example.productservice.repository.BrandRepository;
import org.example.productservice.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public Brand saveOrGetExistingByName(String name) {
        return brandRepository.findByName(name).orElseGet(
                () -> brandRepository.save(Brand.builder().name(name).build())
        );
    }

    @Override
    public Brand updateBrandName(String updatedName, Brand toBeUpdated) {

        String toBeUpdatedName = toBeUpdated.getName();
        if (Objects.equals(updatedName, toBeUpdatedName)) {
            return saveOrGetExistingByName(updatedName);
        }

        return toBeUpdated;
    }
}
