package com.ge.predix.solsvc.experience.datasource.handlers.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.Results;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.Tag;
import com.ge.predix.entity.util.map.Map;
import com.ge.predix.solsvc.experience.datasource.datagrid.service.HygieneResponseObject;

@Component
public class TimeSeriesHygieneParser {

	private Float getValue(Map attributes, String key) {
		Float value = null;
		try {
			value = Float.parseFloat(((ArrayList<String>) attributes.get(key)).get(0));
		} catch (Exception e) {
		}
		return value;
	}

	private Long getValue(List<Object> values) {
		Long value = null;
		try {
			value = ((List<Long>) values.get(0)).get(0);
		} catch (Exception e) {
		}
		return value;
	}

	public Object parseForResponse(List<HygieneResponseObject> list, String name) {
		java.util.Map<String, Object> map = new HashMap<>();
		List<Long> timestamps = new ArrayList<>();
		List<Float> humidity = new ArrayList<>();
		List<Float> noise = new ArrayList<>();
		List<Float> temprature = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			timestamps.add(list.get(i).getTimestamp());
			humidity.add(list.get(i).getHumidity());
			noise.add(list.get(i).getNoise());
			temprature.add(list.get(i).getTemperature());
		}
		map.put("timestamps", timestamps);
		map.put("humidity", humidity);
		map.put("noise", noise);
		map.put("temprature", temprature);
		map.put("name", name);
		return map;
	}

	public Object parseForResponse(List<ResponseObjectCollections> list) {
		List<Object> returnList = new ArrayList<>();
		for (int j = 0; j < list.size(); j++) {
			List<HygieneResponseObject> innerList = list.get(j).getResponseObjects();
			java.util.Map<String, Object> innerMap = new HashMap<>();
			List<Long> timestamps = new ArrayList<>();
			List<Float> humidity = new ArrayList<>();
			List<Float> noise = new ArrayList<>();
			List<Float> temprature = new ArrayList<>();
			for (int i = 0; i < innerList.size(); i++) {
				timestamps.add(innerList.get(i).getTimestamp());
				humidity.add(innerList.get(i).getHumidity());
				noise.add(innerList.get(i).getNoise());
				temprature.add(innerList.get(i).getTemperature());
			}
			innerMap.put("timestamps", timestamps);
			innerMap.put("humidity", humidity);
			innerMap.put("noise", noise);
			innerMap.put("temprature", temprature);
			innerMap.put("name", list.get(j).getName());
			returnList.add(innerMap);
		}
		return returnList;
	}

	public List<ResponseObjectCollections> parse(DatapointsResponse datapointsResponse) {
		List<ResponseObjectCollections> responseObjectCollections = new ArrayList<>();
		try {
			for (int j = 0; j < datapointsResponse.getTags().size(); j++) {
				ResponseObjectCollections responseObjectCollectionsObject = new ResponseObjectCollections();
				Tag tag = datapointsResponse.getTags().get(j);
				List<HygieneResponseObject> list = new ArrayList<HygieneResponseObject>();

				List<Results> results = tag.getResults();
				for (int i = 0; i < results.size(); i++) {

					HygieneResponseObject responseObject = new HygieneResponseObject();

					Map attributes = results.get(i).getAttributes();
					List<Object> values = results.get(i).getValues();

					responseObject.setTemperature(getValue(attributes, "temperature"));
					responseObject.setHumidity(getValue(attributes, "humidity"));
					responseObject.setNoise(getValue(attributes, "noise"));

					responseObject.setTimestamp(getValue(values));
					list.add(responseObject);
				}
				Collections.sort(list, new Comparator<HygieneResponseObject>() {
					public int compare(HygieneResponseObject r1, HygieneResponseObject r2) {
						return (int) (r1.getTimestamp() - r2.getTimestamp());
					}
				});

				responseObjectCollectionsObject.setName(tag.getName());
				responseObjectCollectionsObject.setResponseObjects(list);
				responseObjectCollections.add(responseObjectCollectionsObject);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseObjectCollections;

	}

	public class ResponseObjectCollections {
		private List<HygieneResponseObject> responseObjects = new ArrayList<>();
		private String name;

		public List<HygieneResponseObject> getResponseObjects() {
			return responseObjects;
		}

		public void setResponseObjects(List<HygieneResponseObject> responseObjects) {
			this.responseObjects = responseObjects;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
