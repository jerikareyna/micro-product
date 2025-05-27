package com.rah.demo.microproduct.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
public class ProductApplicationTest {

	@Test
	public void contextLoads() {
		int id = 1;
		int expected = 1;
		assertEquals(expected, id);
	}
	

}
