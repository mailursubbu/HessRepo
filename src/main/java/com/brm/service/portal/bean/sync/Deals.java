package com.brm.service.portal.bean.sync;

import java.util.ArrayList;
import java.util.List;

public class Deals {
	
	private String name;
	private List<Products> productList = new ArrayList<Products>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Products> getProductList() {
		return productList;
	}
	public void setProductList(List<Products> productList) {
		this.productList = productList;
	}
	public void addProductList(Products products) {
		this.productList.add(products);
	}
	
}
