package org.example.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<Page<ProductDto>> getPage(Pageable pageable,
                                                    @RequestParam(required = false) String category,
                                                    @RequestParam(required = false) BigDecimal minPrice,
                                                    @RequestParam(required = false) BigDecimal maxPrice) {

        Page<ProductDto> page = productService.getAllShortProduct(pageable, category, minPrice, maxPrice);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductDetailsDto> createProduct(@RequestBody RequestProductDto newProductData) {
        ProductDetailsDto response = productService.createProduct(newProductData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> productDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductDetailsDto> updateProduct(@PathVariable Integer id,
                                                           @RequestBody RequestProductDto updatedProduct) {
        ProductDetailsDto response = productService.updateProduct(id, updatedProduct);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductDetailsDto> deleteProduct(@PathVariable Integer id) {
        ProductDetailsDto result = productService.deleteById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
