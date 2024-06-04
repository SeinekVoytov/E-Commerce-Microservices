package org.example.userservice.mapper;

import org.example.userservice.dto.ProductLongDto;
import org.example.userservice.model.product.Image;
import org.example.userservice.model.product.ProductLong;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface ProductLongMapper {

    @Mapping(source = "productShort.name", target = "name")
    @Mapping(source = "productShort.images", target = "images")
    @Mapping(source = "productShort.price", target = "price")
    @Mapping(source = "productShort.categories", target = "categories")
    ProductLongDto toDto(ProductLong entity);

    @Mapping(source = "name", target = "productShort.name")
    @Mapping(source = "images", target = "productShort.images")
    @Mapping(source = "price", target = "productShort.price")
    @Mapping(source = "categories", target = "productShort.categories")
    ProductLong toEntity(ProductLongDto entity);

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
