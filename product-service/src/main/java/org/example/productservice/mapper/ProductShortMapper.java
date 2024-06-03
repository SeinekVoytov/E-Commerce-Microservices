package org.example.productservice.mapper;

import org.example.productservice.dto.ProductShortDto;
import org.example.productservice.model.Image;
import org.example.productservice.model.ProductShort;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {PriceMapper.class, CategoryMapper.class}
)
public interface ProductShortMapper {

    ProductShortDto toDto(ProductShort entity);

    ProductShort toEntity(ProductShortDto dto);

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
