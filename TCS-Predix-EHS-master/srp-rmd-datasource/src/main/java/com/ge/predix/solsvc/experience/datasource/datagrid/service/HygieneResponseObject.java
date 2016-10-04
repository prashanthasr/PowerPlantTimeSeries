package com.ge.predix.solsvc.experience.datasource.datagrid.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class HygieneResponseObject {
	private Long timestamp;
	private Float humidity;
	private Float noise;
	private Float temperature;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Float getHumidity() {
		return humidity;
	}

	public void setHumidity(Float humidity) {
		this.humidity = humidity;
	}

	public Float getNoise() {
		return noise;
	}

	public void setNoise(Float noise) {
		this.noise = noise;
	}

	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}
}
