package com.ge.predix.solsvc.machinedata.simulator.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HygeineAtrributesVO {

	@JsonProperty("temperature")
	private Double temperature;
	
	@JsonProperty("noise")
	private Double noise;
	
	@JsonProperty("humidity")
	private Double humidity;

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getNoise() {
		return noise;
	}

	public void setNoise(Double noise) {
		this.noise = noise;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	
	
}
