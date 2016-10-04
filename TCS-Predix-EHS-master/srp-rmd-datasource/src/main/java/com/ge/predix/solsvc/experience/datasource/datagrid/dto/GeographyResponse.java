package com.ge.predix.solsvc.experience.datasource.datagrid.dto;

import java.util.ArrayList;

import com.ge.predix.solsvc.experience.datasource.datagrid.service.EhsValues;

public class GeographyResponse {
	private String name;
	

	private ArrayList<EhsValues> ehsValues = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<EhsValues> getEhsValues() {
		return ehsValues;
	}

	public void setEhsValues(ArrayList<EhsValues> ehsValues) {
		this.ehsValues = ehsValues;
	}

}
