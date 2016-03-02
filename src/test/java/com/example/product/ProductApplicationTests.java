package com.example.product;

import org.apache.http.protocol.HTTP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.example.product.controller.ProductContoller;
import com.example.product.entity.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductApplicationTests {
	
	@Autowired
	private ProductContoller prodContoller;
	
	Product product;
	
	@Before
	public void init(){
		product = new Product();
		long p = new Long(15L);
		product.setId(p);
		product.setCategory("cars");
		product.setDescription("mantano");
		product.setPrimaryImage("Mantano.jpeg");
		product.setTitle("montanocar");
	}
	
	@Test 
	public void testACreateProduct() {
		ResponseEntity<String> result=prodContoller.createProduct(product);
		Assert.assertEquals(HttpStatus.CREATED, result.getStatusCode());
	}
	
	
	@Test
	public void testCGetProduct(){
		
		ResponseEntity<String> result=prodContoller.getProduct(product.getId().toString());
		System.out.println(":::>"+product.getId().toString());
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	public void testDeleteProduct(){
		ResponseEntity<String> result=prodContoller.deleteProduct(product.getId().toString());
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	
	
	
	
}
