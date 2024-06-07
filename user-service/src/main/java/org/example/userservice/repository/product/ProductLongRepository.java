package org.example.userservice.repository.product;

import org.example.userservice.model.product.ProductLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLongRepository extends JpaRepository<ProductLong, Integer> {

}
