package org.example.productservice.service;

import org.example.productservice.dto.PageProductShortDto;

public interface ProductShortService {

    PageProductShortDto getAllShortProduct(int pageNo, int pageSize);
}
