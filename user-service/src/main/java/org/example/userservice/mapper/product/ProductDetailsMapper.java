package org.example.userservice.mapper.product;

import org.example.userservice.dto.product.ProductDetailsDto;
import org.example.userservice.model.product.Image;
import org.example.userservice.model.product.ProductDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {CategoryMapper.class, PriceMapper.class}
)
public interface ProductDetailsMapper {

    @Mapping(source = "productShort.name", target = "name")
    @Mapping(source = "productShort.images", target = "images")
    @Mapping(source = "productShort.price", target = "price")
    @Mapping(source = "productShort.categories", target = "categories")
    ProductDetailsDto toDto(ProductDetails entity);

    @Mapping(source = "name", target = "productShort.name")
    @Mapping(source = "images", target = "productShort.images")
    @Mapping(source = "price", target = "productShort.price")
    @Mapping(source = "categories", target = "productShort.categories")
    ProductDetails toEntity(ProductDetailsDto entity);

    default List<String> imagesToUrls(List<Image> images) {
        return images.stream()
                .map(Image::getUrl)
                .toList();
    }

    default List<Image> urlsToImages(List<String> urls) {
        return urls.stream()
                .map(url -> {
                    Image image = new Image();
                    image.setUrl(url);
                    return image;
                })
                .toList();
    }
}