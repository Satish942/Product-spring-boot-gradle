package com.example.product.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Product implements Serializable{

	/**
	 *  Serial versionId added
	 */
	private static final long serialVersionUID = -707050814497174300L;
	@Id
	@GeneratedValue
	private Long id;
	private String title;
	private String description;
	private String primaryImage;
	private String category;

	public Product() {
	}

	public Product(String title, String description, String primaryImage, String category) {
		super();
		this.title = title;
		this.description = description;
		this.primaryImage = primaryImage;
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPrimaryImage() {
		return primaryImage;
	}

	public void setPrimaryImage(String primaryImage) {
		this.primaryImage = primaryImage;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", title=" + title + ", description=" + description + ", primaryImage="
				+ primaryImage + ", category=" + category + "]";
	}

}