package com.ge.predix.solsvc.experience.datasource.handlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.experience.datasource.handlers.utils.Constants;

@Component
public class MachineSimulater {

	public Map<String, Object> getGeneratedDataArea(String asset_name) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			ArrayList<Map<String, Object>> tags = new ArrayList<Map<String, Object>>();
			Map<String, Object> tags0 = new HashMap<String, Object>();
			map.put("name", asset_name);
			ArrayList<Map<String, Object>> results = new ArrayList<>();
			Map<String, Object> results0 = new HashMap<>();
			Map<String, Object> attributes = new HashMap<String, Object>();

			// PM10, PM2_5,NO2, O3, CO, SO2, NH3, Pb
			attributes.put("O3", getAttributes(Constants.Parameter.O3, 100));
			attributes.put("NH3", getAttributes(Constants.Parameter.NH3, 100));
			attributes.put("NO2", getAttributes(Constants.Parameter.NO2, 100));
			attributes.put("PB", getAttributes(Constants.Parameter.PB, 100));
			attributes.put("CO2", getAttributes(Constants.Parameter.CO2, 100));
			attributes.put("SO2", getAttributes(Constants.Parameter.SO2, 100));
			attributes.put("PM2_5", getAttributes(Constants.Parameter.PM2_5, 100));
			attributes.put("PM10", getAttributes(Constants.Parameter.PM10, 100));

			results0.put("attributes", attributes);
			ArrayList<Object> dataPoints = get100Values();
			results0.put("values", dataPoints);

			results.add(results0);

			tags0.put("results", results);

			map.put("end", (((ArrayList<Object>) dataPoints.get(0)).get(0)));
			map.put("start", (((ArrayList<Object>) dataPoints.get(dataPoints.size() - 1)).get(0)));

			tags.add(tags0);

			map.put("tags", tags);
		} catch (Exception e) {
		}

		return map;
	}

	public Map<String, Object> getGeneratedDataMachine(String asset_name) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			ArrayList<Map<String, Object>> tags = new ArrayList<Map<String, Object>>();
			Map<String, Object> tags0 = new HashMap<String, Object>();
			map.put("name", asset_name);
			ArrayList<Map<String, Object>> results = new ArrayList<>();
			Map<String, Object> results0 = new HashMap<>();
			Map<String, Object> attributes = new HashMap<String, Object>();

			// NO2, SO2, PM2_5
			attributes.put("NO2", getAttributes(Constants.Parameter.NO2, 100));
			attributes.put("SO2", getAttributes(Constants.Parameter.SO2, 100));
			attributes.put("PM2_5", getAttributes(Constants.Parameter.PM2_5, 100));

			results0.put("attributes", attributes);
			ArrayList<Object> dataPoints = get100Values();
			results0.put("values", dataPoints);

			results.add(results0);

			tags0.put("results", results);

			map.put("end", (((ArrayList<Object>) dataPoints.get(0)).get(0)));
			map.put("start", (((ArrayList<Object>) dataPoints.get(dataPoints.size() - 1)).get(0)));

			tags.add(tags0);

			map.put("tags", tags);
		} catch (Exception e) {
		}

		return map;
	}

	public Map<String, Object> getGeneratedDataAreaLast24Hrs(String asset_name) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			ArrayList<Map<String, Object>> tags = new ArrayList<Map<String, Object>>();
			Map<String, Object> tags0 = new HashMap<String, Object>();
			map.put("name", asset_name);
			ArrayList<Map<String, Object>> results = new ArrayList<>();
			Map<String, Object> results0 = new HashMap<>();
			Map<String, Object> attributes = new HashMap<String, Object>();

			// PM10, PM2_5,NO2, O3, CO, SO2, NH3, Pb
			attributes.put("O3", getAttributes(Constants.Parameter.O3, 50));
			attributes.put("NH3", getAttributes(Constants.Parameter.NH3, 50));
			attributes.put("NO2", getAttributes(Constants.Parameter.NO2, 50));
			attributes.put("PB", getAttributes(Constants.Parameter.PB, 50));
			attributes.put("CO2", getAttributes(Constants.Parameter.CO2, 50));
			attributes.put("SO2", getAttributes(Constants.Parameter.SO2, 50));
			attributes.put("PM2_5", getAttributes(Constants.Parameter.PM2_5, 50));
			attributes.put("PM10", getAttributes(Constants.Parameter.PM10, 50));

			results0.put("attributes", attributes);
			ArrayList<Object> dataPoints = get50ValuesFor24Hrs();
			results0.put("values", dataPoints);

			results.add(results0);

			tags0.put("results", results);

			map.put("end", (((ArrayList<Object>) dataPoints.get(0)).get(0)));
			map.put("start", (((ArrayList<Object>) dataPoints.get(dataPoints.size() - 1)).get(0)));

			tags.add(tags0);

			map.put("tags", tags);
		} catch (Exception e) {
		}

		return map;
	}

	public Map<String, Object> getGeneratedDataAreaLast20Sec(String asset_name) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			ArrayList<Map<String, Object>> tags = new ArrayList<Map<String, Object>>();
			Map<String, Object> tags0 = new HashMap<String, Object>();
			map.put("name", asset_name);
			ArrayList<Map<String, Object>> results = new ArrayList<>();
			Map<String, Object> results0 = new HashMap<>();
			Map<String, Object> attributes = new HashMap<String, Object>();

			// PM10, PM2_5,NO2, O3, CO, SO2, NH3, Pb
			attributes.put("O3", getAttributes(Constants.Parameter.O3, 10));
			attributes.put("NH3", getAttributes(Constants.Parameter.NH3, 10));
			attributes.put("NO2", getAttributes(Constants.Parameter.NO2, 10));
			attributes.put("PB", getAttributes(Constants.Parameter.PB, 10));
			attributes.put("CO2", getAttributes(Constants.Parameter.CO2, 10));
			attributes.put("SO2", getAttributes(Constants.Parameter.SO2, 10));
			attributes.put("PM2_5", getAttributes(Constants.Parameter.PM2_5, 10));
			attributes.put("PM10", getAttributes(Constants.Parameter.PM10, 10));

			results0.put("attributes", attributes);
			ArrayList<Object> dataPoints = get10ValuesFor20Sec();
			results0.put("values", dataPoints);

			results.add(results0);

			tags0.put("results", results);

			map.put("end", (((ArrayList<Object>) dataPoints.get(0)).get(0)));
			map.put("start", (((ArrayList<Object>) dataPoints.get(dataPoints.size() - 1)).get(0)));

			tags.add(tags0);

			map.put("tags", tags);
		} catch (Exception e) {
		}

		return map;
	}

	private ArrayList<Double> getAttributes(Constants.Parameter pram, int count) {
		ArrayList<Double> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(getValues(pram));
		}
		return list;
	}

	private ArrayList<Object> get100Values() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long ms = calendar.getTimeInMillis();
		Long interval = 24l * 60l * 60l * 1000l;
		ArrayList<Object> list = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			ArrayList<Long> longList = new ArrayList<>();
			longList.add(ms);
			longList.add(10l);
			longList.add(1l);
			list.add(longList);
			ms -= interval;
		}
		Collections.reverse(list);
		return list;
	}

	private ArrayList<Object> get50ValuesFor24Hrs() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long ms = calendar.getTimeInMillis();
		Long interval = 24l * 60l * 60l * 1000l / 50l;
		ArrayList<Object> list = new ArrayList<>();

		for (int i = 0; i < 50; i++) {
			ArrayList<Long> longList = new ArrayList<>();
			longList.add(ms);
			longList.add(10l);
			longList.add(1l);
			list.add(longList);
			ms -= interval;
		}
		Collections.reverse(list);
		return list;
	}

	private ArrayList<Object> get10ValuesFor20Sec() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long ms = calendar.getTimeInMillis();
		Long interval = 20l * 60l * 1000l / 10l;
		ArrayList<Object> list = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			ArrayList<Long> longList = new ArrayList<>();
			longList.add(ms);
			longList.add(10l);
			longList.add(1l);
			list.add(longList);
			ms -= interval;
		}
		Collections.reverse(list);
		return list;
	}

	public Double getValues(Constants.Parameter range) {
		Double values = new Double(0);
		Random r = new Random();
		int minLimit = 0;
		int maxLimit = 50;
		switch (range) {
		case O3:
			minLimit = 10;
			maxLimit = 50;
			break;
		case NH3:
			minLimit = 10;
			maxLimit = 50;
			break;
		case NO2:
			minLimit = 10;
			maxLimit = 50;
			break;
		case PB:
			minLimit = 500;
			maxLimit = 1000;
			break;
		case CO2:
			minLimit = 1000;
			maxLimit = 2000;
			break;
		case SO2:
			minLimit = 10;
			maxLimit = 50;
			break;
		case PM2_5:
			minLimit = 10;
			maxLimit = 30;
			break;
		case PM10:
			minLimit = 10;
			maxLimit = 20;
			break;

		default:
			break;
		}
		int result = r.nextInt(maxLimit - minLimit) + minLimit;

		switch (range) {
		case CO2:
			values = (double) result / 1000d;
			break;
		case PB:
			values = (double) result / 1000d;
			break;

		default:
			values = (double) result;
			break;

		}

		return values;
	}

	private Double getValues(Constants.Hygiene hygiene) {
		Double values = new Double(0);
		Random r = new Random();
		int minLimit = 0;
		int maxLimit = 50000;
		switch (hygiene) {
		case TEMPERATURE:
			minLimit = 20000;
			maxLimit = 51000;
			break;
		case HUMIDITY:
			minLimit = 25000;
			maxLimit = 70000;
			break;
		case NOISE:
			minLimit = 67000;
			maxLimit = 71000;
			break;

		default:
			break;
		}
		int result = r.nextInt(maxLimit - minLimit) + minLimit;
		values = (double) ((double) result / 1000d);
		return values;
	}

	public ArrayList<Map<String, Object>> getValues(Constants.Hygiene hygiene, int count) {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long ms = calendar.getTimeInMillis();
		Long interval = 20l * 1000l; // 20sec
		for (int i = 0; i < count; i++) {
			Map<String, Object> value = new HashMap<>();
			value.put("y", getValues(hygiene));
			value.put("x", ms);
			ms -= interval;
			list.add(value);
		}
		Collections.reverse(list);
		return list;
	}
}
