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
	private String color;
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", title=" + title + ", description=" + description + ", primaryImage="
				+ primaryImage + ", category=" + category + ", color=" + color + ", size=" + size + "]";
	}

	private int size;

	public Product() {
	}

	public Product(String title, String description, String primaryImage, String category, String color, int size) {
		super();
		this.title = title;
		this.description = description;
		this.primaryImage = primaryImage;
		this.category = category;
		this.color = color;
		this.size = size;
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


	public void setId(Long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}