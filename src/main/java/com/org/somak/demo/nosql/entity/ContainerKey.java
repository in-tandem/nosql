package com.org.somak.demo.nosql.entity;

import java.io.Serializable;

import com.mongodb.lang.NonNull;

public class ContainerKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5636128422599126162L;
	@NonNull
	private String id;
	@NonNull
	private String containerType;
	
	public ContainerKey() {
		
	}
	public ContainerKey(String id, String containerType) {
		super();
		this.id = id;
		this.containerType = containerType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContainerType() {
		return containerType;
	}
	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}
	
	
}
