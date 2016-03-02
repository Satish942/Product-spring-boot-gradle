package com.example.product.controller;

import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;
import com.example.product.entity.Product;
import com.example.product.validate.ProductValidator;

@RestController
@RequestMapping("/product")
public class ProductContoller {

	private final Bucket bucket;
	
	@Autowired
	public ProductContoller(final Bucket bucket) {
		this.bucket = bucket;
	}

	@RequestMapping("/allproducts")
	ResponseEntity<String> getAllProducts(@RequestParam(required = false) Integer offset,
			@RequestParam(required = false) Integer limit) {
		ViewQuery query = ViewQuery.from("product", "by_title");
		if (limit != null && limit > 0) {
			query.limit(limit);
		}
		if (offset != null && offset > 0) {
			query.skip(offset);
		}
		ViewResult result = bucket.query(query);
		if (!result.success()) {
			// TODO maybe detect type of error and change error code accordingly
			return new ResponseEntity<String>(result.error().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			JsonArray keys = JsonArray.create();
			Iterator<ViewRow> iter = result.rows();
			while (iter.hasNext()) {
				ViewRow row = iter.next();
				JsonObject prod = JsonObject.create();
				prod.put("id", row.id());
				prod.put("content", row.value());
				keys.add(prod);
			}
			return new ResponseEntity<String>(keys.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProduct(@PathVariable("id") String id) {
		JsonDocument doc = bucket.get(id);
		if (doc != null) {
			return new ResponseEntity<String>(doc.content().toString(), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable("id") String prodId) {
		JsonDocument deleted = bucket.remove(prodId);
		return new ResponseEntity<String>("" + deleted.cas(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createProduct(@RequestBody Product inputObject) {
		String id = "";
		try {
			if(new ProductValidator().validte(inputObject)){
			JsonObject prod =JsonObject.create();
			prod.put("id", inputObject.getId());
			prod.put("title", inputObject.getTitle());
			prod.put("description", inputObject.getDescription());
			prod.put("primaryImage", inputObject.getPrimaryImage());
			prod.put("category", inputObject.getCategory());
			bucket.insert(JsonDocument.create(inputObject.getId().toString(), prod));
			return new ResponseEntity<String>(inputObject.getId().toString(), HttpStatus.CREATED);
			}
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (DocumentAlreadyExistsException e) {
			return new ResponseEntity<String>("Id " + id + " already exist", HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}

}
