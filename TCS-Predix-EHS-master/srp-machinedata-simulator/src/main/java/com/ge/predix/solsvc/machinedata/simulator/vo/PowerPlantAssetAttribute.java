package com.ge.predix.solsvc.machinedata.simulator.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerPlantAssetAttribute {
	
	@JsonProperty("assetSerialId")
	private String assetSerialId;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("measurement")
	private String measurement;
	
	@JsonProperty("sensorId")
	private String sensorId;
	
	public String getAssetSerialId() {
		return assetSerialId;
	}
	public void setAssetSerialId(String assetSerialId) {
		this.assetSerialId = assetSerialId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMeasurement() {
		return measurement;
	}
	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	
	
}
