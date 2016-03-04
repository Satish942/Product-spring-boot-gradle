package com.example.product.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;
import com.example.product.entity.Product;
import com.example.product.validate.ProductValidator;
import com.google.gson.Gson;

@RestController
@RequestMapping("/product")
public class ProductContoller {

	private final Bucket bucket;

	@Autowired
	public ProductContoller(final Bucket bucket) {
		this.bucket = bucket;
	}

	@Autowired
	private ProductValidator prodValidator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(prodValidator);
	}

	@RequestMapping("/allproducts")
	@ResponseBody
	public ResponseEntity<String> getAllProducts(@RequestParam(required = false) Integer offset,
			@RequestParam(required = false) Integer limit)
					throws JsonParseException, JsonMappingException, IOException {
		ViewQuery query = ViewQuery.from("product_id", "by_id");
		if (limit != null && limit > 0) {
			query.limit(limit);
		}
		if (offset != null && offset > 0) {
			query.skip(offset);
		}
		ViewResult result = bucket.query(query);
		if (!result.success()) {
			return new ResponseEntity<String>(result.error().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			List<Product> listTitle = getAttributeList(result);
			return new ResponseEntity<String>(objectMapper.writeValueAsString(listTitle), HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> getProduct(@PathVariable("id") String id) throws JsonGenerationException, JsonMappingException, IOException {
		JsonDocument doc = bucket.get(id);
		if (doc != null) {
			return new ResponseEntity<String>(doc.content().toString(), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
	@ResponseBody
	public ResponseEntity<String> deleteProduct(@PathVariable("id") String prodId) {
		JsonDocument deleted = bucket.remove(prodId);
		return new ResponseEntity<String>("" + deleted.cas(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> createProduct(@Valid @RequestBody Product inputObject, BindingResult result) {
		String id = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			if (result.hasErrors()) {
				return new ResponseEntity<String>(result.getFieldError().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			JsonObject prod = productToJsonObjConverter(inputObject);
			bucket.insert(JsonDocument.create(inputObject.getId().toString(), prod));
			return new ResponseEntity<String>(objectMapper.writeValueAsString(inputObject.getId().toString()), HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (DocumentAlreadyExistsException e) {
			return new ResponseEntity<String>("Id " + id + " already exist", HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/searchtitle/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> searchProductByTitleToken(@PathVariable final String token)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ViewQuery query = ViewQuery.from("products", "by_title");
		List<Product> listTitle = new ArrayList<Product>();
		ViewResult result = bucket.query(query);
		if (!result.success()) {
			return new ResponseEntity<String>(result.error().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			listTitle = getAttributeList(result);
			
			List<Product> resul = listTitle.stream().filter(P -> P.getTitle().contains(token))
					.collect(Collectors.toList());
			return new ResponseEntity<String>(objectMapper.writeValueAsString(resul.toString()), HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> updateProduct(@RequestBody Product inputObject) throws JsonGenerationException, JsonMappingException, IOException {
		JsonDocument doc = bucket.get(inputObject.getId().toString());
		ObjectMapper objectMapper = new ObjectMapper();
		if (doc != null) {
			JsonObject prod = productToJsonObjConverter(inputObject);
			bucket.upsert(JsonDocument.create(inputObject.getId().toString(), prod));
			return new ResponseEntity<String>(objectMapper.writeValueAsString(inputObject.getId().toString()), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}

	}

	private JsonObject productToJsonObjConverter(Product product) {
		JsonObject prod = JsonObject.create();
		prod.put("id", product.getId());
		prod.put("title", product.getTitle());
		prod.put("description", product.getDescription());
		prod.put("primaryImage", product.getPrimaryImage());
		prod.put("category", product.getCategory());
		prod.put("color", product.getColor());
		prod.put("size", product.getSize());
		return prod;
	}

	private List<Product> getAttributeList(ViewResult result)
			throws JsonParseException, JsonMappingException, IOException {
		List<Product> listTitle = new ArrayList<Product>();
		List<ViewRow> listRows = result.allRows();
	
		listRows.forEach(name -> {
			Product prod = null;
			Gson gson = new Gson();
			prod = gson.fromJson(name.value().toString(), Product.class);
			listTitle.add(prod);
		});
		
		return listTitle;
	}

	@RequestMapping(method = RequestMethod.GET, value = "attribute/{attribute}/{property}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fileterAttribute(@PathVariable final String attribute,
			@PathVariable final String property) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ViewQuery query = ViewQuery.from("product_id", "by_id");
		List<Product> listTitle = new ArrayList<Product>();
		ViewResult result = bucket.query(query);
		if (!result.success()) {
			return new ResponseEntity<String>(result.error().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			if (attribute.equalsIgnoreCase("color")) {
				listTitle = getAttributeList(result);

				List<Product> sizeColor = listTitle.stream().filter(P -> P.getColor().equals(property))
						.collect(Collectors.toList());
				return new ResponseEntity<String>(mapper.writeValueAsString(sizeColor), HttpStatus.OK);
			} else if (attribute.equalsIgnoreCase("size")) {
				listTitle = getAttributeList(result);

				List<Product> sizeResult = listTitle.stream().filter(P -> P.getSize() == Integer.parseInt(property))
						.collect(Collectors.toList());
				return new ResponseEntity<String>(mapper.writeValueAsString(sizeResult), HttpStatus.OK);
			}
			return new ResponseEntity<String>(" choose between:color,size or Inputerror",
					HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

}
