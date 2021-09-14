package com.org.somak.demo.nosql.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

@Document
public class Container {

	// this gets mapped to _id field in db.test.container
	@Id
	private ContainerKey key;
	@NonNull
	private String name;
	private float size;
	private String unit;
	
	
	public ContainerKey getKey() {
		return key;
	}
	public void setKey(ContainerKey key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getSize() {
		return size;
	}
	public void setSize(float size) {
		this.size = size;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	// how to deal with _id and id 
	// how to map enum

	
}
