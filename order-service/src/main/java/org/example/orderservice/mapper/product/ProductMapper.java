package org.example.orderservice.mapper.product;

import org.example.orderservice.dto.product.ProductDto;
import org.example.orderservice.model.product.Image;
import org.example.orderservice.model.product.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {PriceMapper.class, CategoryMapper.class}
)
public interface ProductMapper {

    ProductDto toDto(Product entity);

    Product toEntity(ProductDto dto);

    default Set<String> imagesToUrls(Set<Image> images) {
        return images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toSet());
    }

    default Set<Image> urlsToImages(Set<String> urls) {
        return urls.stream()
                .map(url -> {
                    Image image = new Image();
                    image.setUrl(url);
                    return image;
                })
                .collect(Collectors.toSet());
    }
}
