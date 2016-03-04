package com.example.product.validate;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.product.entity.Product;

@Component
public class ProductValidator implements Validator {

	@Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

	@Override
	public void validate(Object target, Errors errors) {
		Product prod = (Product) target;
		
		Long productId = prod.getId();
        
        ValidationUtils.rejectIfEmpty(errors, "title", "Title Can't be empty");
        ValidationUtils.rejectIfEmpty(errors, "primaryImage", "Primary Image can't be empty");
        ValidationUtils.rejectIfEmpty(errors, "description", "Description can't be empty" );
        ValidationUtils.rejectIfEmpty(errors, "color", "Color can't be empty" );
        ValidationUtils.rejectIfEmpty(errors, "size", "Size can't be empty" );
        
        if (productId != null && productId < 1)
            errors.rejectValue("id", "ID input is incorrect");
	}

}
