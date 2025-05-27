package com.rah.demo.microproduct.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.rah.demo.microproduct.app.model.ProductVo;

public class ProductControllerImplTest {

	private ProductControllerImpl controller;

	@BeforeEach
	void init() {
		this.controller = new ProductControllerImpl();
	}

	@Test
	void addProductTest() {
		ProductVo productVo = new ProductVo();
		ResponseEntity<ProductVo> response = this.controller.addProduct(productVo);
		assertNotNull(response);
		int statusExpected = 201;
		assertEquals(statusExpected, response.getStatusCode().value());
	}
}
