package com.example.product.validate;

import org.springframework.stereotype.Component;

import com.example.product.entity.Product;

public class ProductValidator {

	public boolean validte(Product inputData) throws IllegalArgumentException{
		if (inputData.getPrimaryImage() != null || inputData.getTitle() != null 
				|| inputData.getDescription()!=null|| inputData.getId()!=null) {
	           return true;
	        }
		return false;
	}

}
