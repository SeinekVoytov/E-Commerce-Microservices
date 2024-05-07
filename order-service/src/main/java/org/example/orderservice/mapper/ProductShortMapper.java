package org.example.orderservice.mapper;

import org.example.orderservice.dto.ProductShortDto;
import org.example.orderservice.model.product.Image;
import org.example.orderservice.model.product.ProductShort;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductShortMapper {

    ProductShortDto toDto(ProductShort entity);
    ProductShort toEntity(ProductShortDto entity);

    default List<String> mapImagesToUrls(List<Image> images) {
        return images.stream()
                .map(Image::getUrl)
                .toList();
    }

    default List<Image> mapUrlsToImages(List<String> urls) {
        return urls.stream()
                .map(url -> {
                    Image image = new Image();
                    image.setUrl(url);
                    return image;
                })
                .toList();
    }
}
