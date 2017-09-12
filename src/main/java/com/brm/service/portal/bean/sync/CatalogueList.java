package com.brm.service.portal.bean.sync;

import java.util.ArrayList;
import java.util.List;

public class CatalogueList {
	
	private List<Catalogue> products = new ArrayList<Catalogue>();

	public List<Catalogue> getProducts() {
		return products;
	}

	public void setProducts(List<Catalogue> products) {
		this.products = products;
	}
	
}
