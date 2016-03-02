package com.example.product.validate;

import org.springframework.context.annotation.ComponentScan;

import com.example.product.entity.Product;

@ComponentScan
public class ProductValidator {

	public boolean validte(Product inputData) throws IllegalArgumentException{
		if (inputData.getPrimaryImage() != null || inputData.getTitle() != null 
				|| inputData.getDescription()!=null|| inputData.getId()!=null) {
	           return true;
	        }
		return false;
	}

}
