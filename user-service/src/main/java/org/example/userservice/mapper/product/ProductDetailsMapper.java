package org.example.userservice.mapper.product;

import org.example.userservice.dto.product.ProductDetailsDto;
import org.example.userservice.model.product.Image;
import org.example.userservice.model.product.ProductDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {CategoryMapper.class, PriceMapper.class}
)
public interface ProductDetailsMapper {

    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.images", target = "images")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.categories", target = "categories")
    ProductDetailsDto toDto(ProductDetails entity);

    @Mapping(source = "name", target = "product.name")
    @Mapping(source = "images", target = "product.images")
    @Mapping(source = "price", target = "product.price")
    @Mapping(source = "categories", target = "product.categories")
    ProductDetails toEntity(ProductDetailsDto entity);

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