package org.example.productservice.controller;

import jakarta.validation.Valid;
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
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getPage(Pageable pageable,
                                                    @RequestParam(required = false) String category,
                                                    @RequestParam(required = false) BigDecimal minPrice,
                                                    @RequestParam(required = false) BigDecimal maxPrice,
                                                    @RequestParam(required = false) String brand,
                                                    @RequestParam(required = false) String country){

        Page<ProductDto> page = productService.getAllProducts(
                pageable, category, minPrice, maxPrice, brand, country
        );

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(@RequestParam String keyword) {
        List<ProductDto> foundProducts = productService.search(keyword);
        return new ResponseEntity<>(foundProducts, HttpStatus.OK);
    }

    @PostMapping("/reindex")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ProductDto>> reindex() {
        List<ProductDto> reindexed = productService.reindex();
        return new ResponseEntity<>(reindexed, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductDetailsDto> createProduct(@RequestBody @Valid RequestProductDto newProductData) {
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
                                                           @RequestBody @Valid RequestProductDto updatedProduct) {
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
