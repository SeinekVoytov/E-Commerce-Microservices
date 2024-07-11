package org.example.productservice.service;

import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.exception.InvalidQueryParameterException;
import org.example.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface ProductService {

    Map<String, Comparator<Product>> AVAILABLE_SORT_PARAMETERS =
            Map.of(
                    "name", Comparator.comparing(Product::getName),
                    "price.amount", Comparator.comparing(p -> p.getPrice().getAmount())
            );

    static void validateSortParameters(Sort sort) {
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (!AVAILABLE_SORT_PARAMETERS.containsKey(property)) {
                throw new InvalidQueryParameterException("sort", property);
            }
        }
    }

    static Comparator<Product> getComparatorBySort(Sort sort) {

        Comparator<Product> comparator = (p1, p2) -> 0;           // empty comparator
        Iterator<Sort.Order> orderIterator = sort.stream().iterator();

        if (orderIterator.hasNext()) {
            comparator = getComparatorForOrder(orderIterator.next());

            while (orderIterator.hasNext()) {
                comparator.thenComparing(getComparatorForOrder(orderIterator.next()));
            }
        }

        return comparator;
    }

    private static Comparator<Product> getComparatorForOrder(Sort.Order order) {
        String property = order.getProperty();
        Comparator<Product> comparator = ProductService.AVAILABLE_SORT_PARAMETERS.get(property);
        return (order.isDescending()) ? comparator.reversed() : comparator;
    }

    Page<ProductDto> getAllProducts(Pageable pageable,
                                    String category,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice,
                                    String brand,
                                    String country);

    List<ProductDto> search(String keyword);

    List<ProductDto> reindex();

    ProductDetailsDto getById(int id);

    ProductDetailsDto createProduct(RequestProductDto newProductData);

    ProductDetailsDto deleteById(int id);

    ProductDetailsDto updateProduct(int id, RequestProductDto updatedProduct);
}
