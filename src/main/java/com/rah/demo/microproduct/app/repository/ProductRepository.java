package com.rah.demo.microproduct.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rah.demo.microproduct.app.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

}
