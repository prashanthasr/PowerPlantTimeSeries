package com.ge.predix.solsvc.machinedata.simulator.vo;


import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class AQIBody {

	@JsonProperty("name")
	private String name;
	@JsonProperty("datapoints")
	private ArrayList<ArrayList<Object>> datapoints = new ArrayList<ArrayList<Object>>();
	
	@JsonProperty("attributes")
	private PowerPlantAssetAttribute attributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ArrayList<Object>> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(ArrayList<ArrayList<Object>> datapoints) {
		this.datapoints = datapoints;
	}

	public PowerPlantAssetAttribute getAttributes() {
		return attributes;
	}

	public void setAttributes(PowerPlantAssetAttribute attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "AQIBody [name=" + name + ", datapoints=" + datapoints + ", attributes=" + attributes + "]";
	}
	
	

	
	

	

	
	
}
