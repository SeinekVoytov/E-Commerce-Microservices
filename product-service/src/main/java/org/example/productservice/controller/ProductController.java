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

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<Page<ProductDto>> getPage(Pageable pageable) {
        return new ResponseEntity<>(productService.getAllShortProduct(pageable), HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductDetailsDto> createProduct(@RequestBody RequestProductDto newProductData) {
        ProductDetailsDto response = productService.createProduct(newProductData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> productDetails(@PathVariable int id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductDetailsDto> updateProduct(@PathVariable int id,
                                                           @RequestBody RequestProductDto updatedProduct) {
        ProductDetailsDto response = productService.updateProduct(id, updatedProduct);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductDetailsDto> deleteProduct(@PathVariable int id) {
        ProductDetailsDto result = productService.deleteById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

