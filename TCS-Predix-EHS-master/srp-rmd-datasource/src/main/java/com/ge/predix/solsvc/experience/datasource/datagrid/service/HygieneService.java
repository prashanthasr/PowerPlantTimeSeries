package com.ge.predix.solsvc.experience.datasource.datagrid.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.experience.datasource.handlers.HygieneDatasourceHandler;
import com.ge.predix.solsvc.experience.datasource.handlers.HygieneHandler;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.TimeSeriesHygieneParser;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.TimeSeriesHygieneParser.ResponseObjectCollections;

@Consumes({ "application/json" })
@Produces({ "application/json" })
@Path("/hygiene")
@Component(value = "hygieneService")
public class HygieneService {
	@Autowired
	HygieneDatasourceHandler hygieneDatasourceHandler;

	@Autowired
	TimeSeriesHygieneParser timeSeriesHygieneParser;

	@Autowired
	private HygieneHandler hygieneHandler;

	@GET
	@Path("/last24Hrs")
	public ResponseEntity<ArrayList<Object>> last24Hrs() {
		ArrayList<Object> list = hygieneHandler.getlast24HrsData();
		return new ResponseEntity<ArrayList<Object>>(list, HttpStatus.OK);
	}

	@GET
	@Path("/last20Sec")
	public ResponseEntity<Object> last20Sec(@HeaderParam(value = "authorization") String authorization) throws JsonProcessingException {
		Long twentySec = 20l * 1000l; // 20sec in ms
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - twentySec);
		String end_time = String.valueOf(currentTime);

		// log.info("id : " + assetName);
		// log.info("authorization : " + authorization);
		// log.info("start_time : " + start_time);
		// log.info("end_time : " + end_time);

		DatapointsResponse datapointsResponse = hygieneDatasourceHandler.getGeographyResponse(authorization, start_time, end_time);
		List<ResponseObjectCollections> list = timeSeriesHygieneParser.parse(datapointsResponse);
		return new ResponseEntity<Object>(timeSeriesHygieneParser.parseForResponse(list), HttpStatus.OK);
	}

	@GET
	@Path("/last10Min")
	public ResponseEntity<Object> last10Min(@HeaderParam(value = "authorization") String authorization) throws Throwable {
		Long twentyFourHours = 10l * 60l * 1000l; // 10 min in ms
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - twentyFourHours);
		String end_time = String.valueOf(currentTime);

		DatapointsResponse datapointsResponse = hygieneDatasourceHandler.getGeographyResponse(authorization, start_time, end_time);
		List<ResponseObjectCollections> list = timeSeriesHygieneParser.parse(datapointsResponse);
		return new ResponseEntity<Object>(timeSeriesHygieneParser.parseForResponse(list), HttpStatus.OK);
	}

	@GET
	@Path("/last1Hrs")
	public ResponseEntity<Object> last1Hr(@HeaderParam(value = "authorization") String authorization) throws Throwable {
		Long oneHour = 60l * 60l * 1000l; // 1hr
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - oneHour);
		String end_time = String.valueOf(currentTime);

		DatapointsResponse datapointsResponse = hygieneDatasourceHandler.getGeographyResponse(authorization, start_time, end_time);
		List<ResponseObjectCollections> list = timeSeriesHygieneParser.parse(datapointsResponse);
		return new ResponseEntity<Object>(timeSeriesHygieneParser.parseForResponse(list), HttpStatus.OK);
	}

	@GET
	@Path("/dashboardValues")
	public ResponseEntity<ArrayList<Object>> dashboardValues(@HeaderParam(value = "authorization") String authorization) throws JsonProcessingException {

		Long oneHour = 60l * 60l * 1000l; // 1hr
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - oneHour);
		String end_time = String.valueOf(currentTime);

		DatapointsResponse datapointsResponse = hygieneDatasourceHandler.getGeographyResponse(authorization, start_time, end_time);
		List<ResponseObjectCollections> listData = timeSeriesHygieneParser.parse(datapointsResponse);
		ArrayList<Object> list = new ArrayList<>();
		for (int i = 0; i < listData.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", listData.get(i).getName());
			HygieneResponseObject responseObject = calculateAverage(listData.get(i).getResponseObjects());
			DecimalFormat df = new DecimalFormat("#.#");
			df.setRoundingMode(RoundingMode.CEILING);

			map.put("temperature", df.format(responseObject.getTemperature()));
			map.put("humidity", df.format(responseObject.getHumidity()));
			map.put("noise", df.format(responseObject.getNoise()));
			list.add(map);
		}

		return new ResponseEntity<ArrayList<Object>>(list, HttpStatus.OK);
	}

	private HygieneResponseObject calculateAverage(List<HygieneResponseObject> list) {
		HygieneResponseObject rObject = new HygieneResponseObject();
		Float sumT = 0f;
		Float sumH = 0f;
		Float sumN = 0f;
		for (HygieneResponseObject data : list) {
			sumT += data.getTemperature();
			sumH += data.getHumidity();
			sumN += data.getNoise();
		}

		rObject.setTemperature((float) (sumT / (float) list.size()));
		rObject.setHumidity((float) (sumH / (float) list.size()));
		rObject.setNoise((float) (sumN / (float) list.size()));
		return rObject;
	}

}
