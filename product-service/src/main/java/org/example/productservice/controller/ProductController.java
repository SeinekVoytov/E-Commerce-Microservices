package org.example.productservice.controller;

import org.example.productservice.dto.CreateProductDto;
import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.dto.ProductLongDto;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<PageProductShortDto> getPage(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "order", defaultValue = "price.amount:asc", required = false) String order
    ) {
        return new ResponseEntity<>(productService.getAllShortProduct(pageNo, pageSize, order), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ProductLongDto> createProduct(@RequestBody CreateProductDto newProductData) {
        ProductLongDto response = productService.createProduct(newProductData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductLongDto> productDetails(@PathVariable int id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductLongDto> updateProduct(@PathVariable int id,
                                                        @RequestBody ProductLongDto updatedProduct) {
        ProductLongDto response = productService.updateProduct(id, updatedProduct);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
