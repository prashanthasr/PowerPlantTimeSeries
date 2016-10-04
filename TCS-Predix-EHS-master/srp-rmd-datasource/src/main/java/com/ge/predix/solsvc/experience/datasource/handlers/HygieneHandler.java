package com.ge.predix.solsvc.experience.datasource.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.predix.solsvc.experience.datasource.handlers.utils.Constants;

@Service
public class HygieneHandler {
	@Autowired
	private MachineSimulater machineSimulater;

	public ArrayList<Object> getlast24HrsData() {
		ArrayList<Object> list = new ArrayList<>();

		int areaCount = Constants.hygieneAreas.length;
		for (int i = 0; i < areaCount; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", Constants.hygieneAreas[i]);
			map.put("temperature", machineSimulater.getValues(Constants.Hygiene.TEMPERATURE, 20));
			map.put("humidity", machineSimulater.getValues(Constants.Hygiene.HUMIDITY, 20));
			map.put("noise", machineSimulater.getValues(Constants.Hygiene.NOISE, 20));
			list.add(map);
		}
		return list;
	}

	public ArrayList<Object> getlast20SecData() {
		ArrayList<Object> list = new ArrayList<>();

		int areaCount = Constants.hygieneAreas.length;
		for (int i = 0; i < areaCount; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", Constants.hygieneAreas[i]);
			map.put("temperature", machineSimulater.getValues(Constants.Hygiene.TEMPERATURE, 1));
			map.put("humidity", machineSimulater.getValues(Constants.Hygiene.HUMIDITY, 1));
			map.put("noise", machineSimulater.getValues(Constants.Hygiene.NOISE, 1));
			list.add(map);
		}
		return list;
	}

	public ArrayList<Object> dashboardValues() {
		ArrayList<Object> list = new ArrayList<>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", Constants.hygieneAreas[0]);
		map.put("temperature", 22);
		map.put("humidity", 44);
		map.put("noise", 68);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", Constants.hygieneAreas[1]);
		map.put("temperature", 50);
		map.put("humidity", 70);
		map.put("noise", 69);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", Constants.hygieneAreas[2]);
		map.put("temperature", 27);
		map.put("humidity", 25);
		map.put("noise", 68);
		list.add(map);

		return list;
	}
}
