package com.example.product;

import java.io.IOException;

import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
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
		ResponseEntity<String> result=prodContoller.createProduct(product, null);
		Assert.assertEquals(HttpStatus.CREATED, result.getStatusCode());
	}
	
	
	@Test
	public void testCGetProduct() throws JsonGenerationException, JsonMappingException, IOException{
		
		ResponseEntity<String> result=prodContoller.getProduct(product.getId().toString());
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	public void testBProductGetAll() throws JsonParseException, JsonMappingException, IOException{
		ResponseEntity<String> result=prodContoller.getAllProducts(5, 100);
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	

	@Test
	public void testBProductGetByTitleToken() throws JsonParseException, JsonMappingException, IOException{
		ResponseEntity<String> result=prodContoller.searchProductByTitleToken("mont");
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	public void testBProductUpdate() throws JsonParseException, JsonMappingException, IOException{
		product.setColor("green");
		product.setSize(99);
		ResponseEntity<String> result=prodContoller.updateProduct(product);
		Assert.assertNotNull(result);
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	public void testBFilerAttribute() throws JsonParseException, JsonMappingException, IOException{
		product.setColor("red");
		product.setSize(99);
		ResponseEntity<String> result=prodContoller.fileterAttribute("color", "red");
		Assert.assertNotNull(result);
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	@Test
	public void testBFilerAttributeSize() throws JsonParseException, JsonMappingException, IOException{
		product.setColor("green");
		product.setSize(99);
		ResponseEntity<String> result=prodContoller.fileterAttribute("size", "99");
		Assert.assertNotNull(result);
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	public void testDeleteProduct(){
		ResponseEntity<String> result=prodContoller.deleteProduct(product.getId().toString());
		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	
	
	
	
}
