package org.example.productservice.controller;

import org.example.productservice.dto.PageProductShortDto;
import org.example.productservice.service.ProductShortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductShortService productShortService;

    @Autowired
    public ProductController(ProductShortService productShortService) {
        this.productShortService = productShortService;
    }

    @GetMapping()
    public ResponseEntity<PageProductShortDto> getPage(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return new ResponseEntity<>(productShortService.getAllShortProduct(pageNo, pageSize), HttpStatus.OK);
    }
}
