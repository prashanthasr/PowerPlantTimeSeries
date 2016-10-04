package com.ge.predix.solsvc.experience.datasource.datagrid.service;

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
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.experience.datasource.handlers.GeographyDatasourceHandler;
import com.ge.predix.solsvc.experience.datasource.handlers.MachineSimulater;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.AqiCalculations;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.AqiCalculations.OverallAqiResponse;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.TimeSeriesAqiParser;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.TimeSeriesAqiParser.AqiResponseObjectCollections;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.TimeSeriesAqiParser.ResponseObject;

@Consumes({ "application/json" })
@Produces({ "application/json" })
@Path("/geography")
@Component(value = "geographyTimeSeriesService")
public class GeographyTimeSeriesService {
	private static Logger log = LoggerFactory.getLogger(GeographyTimeSeriesService.class);

	@Autowired
	GeographyDatasourceHandler geographyDatasourceHandler;

	@Autowired
	TimeSeriesAqiParser timeSeriesAqiParser;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	AqiCalculations aqiCalculations;

	@GET
	@Path("/ping")
	public String ping() {

		return "SUCCESS";
	}

	@GET
	public ResponseEntity<DatapointsResponse> getGeographySummary(@QueryParam("asset_name") String assetName, @HeaderParam(value = "authorization") String authorization,
			@QueryParam("start_time") String start_time, @QueryParam("end_time") String end_time) throws Throwable {

		log.info("id : " + assetName);
		log.info("authorization : " + authorization);
		log.info("start_time : " + start_time);
		log.info("end_time : " + end_time);

		DatapointsResponse datapointsResponse = geographyDatasourceHandler.getGeographyResponse(assetName, authorization, start_time, end_time);

		return new ResponseEntity<DatapointsResponse>(datapointsResponse, HttpStatus.OK);
	}

	// Last Week highest AQI

	@GET
	@Path("/last24Hrs")
	public ResponseEntity<Object> getGeographySummaryLast24Hr(@QueryParam("asset_name") String assetName, @HeaderParam(value = "authorization") String authorization) throws Throwable {
		Long twentyFourHours = 24l * 60l * 60l * 1000l; // 24 hs in ms
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - twentyFourHours);
		String end_time = String.valueOf(currentTime);

		log.info("id : " + assetName);
		log.info("authorization : " + authorization);
		log.info("start_time : " + start_time);
		log.info("end_time : " + end_time);

		DatapointsResponse datapointsResponse = geographyDatasourceHandler.getGeographyResponse(assetName, authorization, start_time, end_time);
		List<ResponseObject> list = timeSeriesAqiParser.parse(datapointsResponse);
		OverallAqiResponse overallAqiResponse = aqiCalculations.calculateAqiMachine(list, Long.parseLong(start_time), Long.parseLong(end_time));
		return new ResponseEntity<Object>(overallAqiResponse, HttpStatus.OK);
	}

	@GET
	@Path("/lastWeekHighestAQI")
	public ResponseEntity<Object> lastWeekHighestAQI(@QueryParam("asset_name") String assetName, @HeaderParam(value = "authorization") String authorization) throws Throwable {
		Long twentyFourHours = 24l * 60l * 60l * 1000l;

		// Long lastWeek = twentyFourHours * 7; // 7 days

		// Since timeseries don't have correct value for last week. So we using
		// last day.
		Long lastWeek = twentyFourHours * 1; // 1 days

		Long tenMin = 10l * 60l * 1000l;
		Long lastWeek10MinBefore = lastWeek + tenMin; // 7 days

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();

		String start_time = String.valueOf(currentTime - lastWeek10MinBefore);
		String end_time = String.valueOf(currentTime - lastWeek);

		System.out.println("id : " + assetName);
		System.out.println("authorization : " + authorization);
		System.out.println("start_time : " + start_time);
		System.out.println("end_time : " + end_time);

		DatapointsResponse datapointsResponse = geographyDatasourceHandler.getGeographyResponse(assetName, authorization, start_time, end_time);
		List<ResponseObject> list = timeSeriesAqiParser.parse(datapointsResponse);
		OverallAqiResponse overallAqiResponse = aqiCalculations.calculateAqiMachine(list, Long.parseLong(start_time), Long.parseLong(end_time));

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("maxAqi", overallAqiResponse.getMaxAqi());
		resMap.put("assetName", assetName);

		return new ResponseEntity<Object>(resMap, HttpStatus.OK);
	}

	@GET
	@Path("/last10Min")
	public ResponseEntity<Object> getGeographySummaryLast10Min(@QueryParam("asset_name") String assetName, @HeaderParam(value = "authorization") String authorization) throws Throwable {
		Long twentyFourHours = 10l * 60l * 1000l; // 24 hs in ms
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - twentyFourHours);
		String end_time = String.valueOf(currentTime);

		log.info("id : " + assetName);
		log.info("authorization : " + authorization);
		log.info("start_time : " + start_time);
		log.info("end_time : " + end_time);

		DatapointsResponse datapointsResponse = geographyDatasourceHandler.getGeographyResponse(assetName, authorization, start_time, end_time);
		List<ResponseObject> list = timeSeriesAqiParser.parse(datapointsResponse);
		OverallAqiResponse overallAqiResponse = aqiCalculations.calculateAqiMachine(list, Long.parseLong(start_time), Long.parseLong(end_time));
		return new ResponseEntity<Object>(overallAqiResponse, HttpStatus.OK);
	}

	@GET
	@Path("/last1Hrs")
	public ResponseEntity<Object> getGeographySummaryLast1Hr(@QueryParam("asset_name") String assetName, @HeaderParam(value = "authorization") String authorization) throws Throwable {
		Long twentyFourHours = 1l * 60l * 60l * 1000l; // 24 hs in ms
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - twentyFourHours);
		String end_time = String.valueOf(currentTime);

		log.info("id : " + assetName);
		log.info("authorization : " + authorization);
		log.info("start_time : " + start_time);
		log.info("end_time : " + end_time);

		DatapointsResponse datapointsResponse = geographyDatasourceHandler.getGeographyResponse(assetName, authorization, start_time, end_time);
		List<ResponseObject> list = timeSeriesAqiParser.parse(datapointsResponse);
		OverallAqiResponse overallAqiResponse = aqiCalculations.calculateAqiMachine(list, Long.parseLong(start_time), Long.parseLong(end_time));
		return new ResponseEntity<Object>(overallAqiResponse, HttpStatus.OK);
	}

	@GET
	@Path("/appDashBoard")
	public ResponseEntity<Object> appDashboard(@HeaderParam(value = "authorization") String authorization) throws Throwable {
		Long min20 = 20l * 60l * 1000l; // 20sec
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long currentTime = calendar.getTimeInMillis();
		String start_time = String.valueOf(currentTime - min20);
		String end_time = String.valueOf(currentTime);

		DatapointsResponse datapointsResponse = geographyDatasourceHandler.getGeographyResponse(authorization, start_time, end_time);
		List<AqiResponseObjectCollections> list = timeSeriesAqiParser.parseFromMultipleTags(datapointsResponse);
		List<OverallAqiResponse> parseForResponse = timeSeriesAqiParser.parseForResponse(list, Long.parseLong(start_time), Long.parseLong(end_time));
		return new ResponseEntity<Object>(parseForResponse, HttpStatus.OK);
	}

	@GET
	@Path("/last20Sec")
	public ResponseEntity<Object> getGeographySummaryLast20Sec(@QueryParam("asset_name") String assetName, @HeaderParam(value = "authorization") String authorization) throws Throwable {
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

		DatapointsResponse datapointsResponse = geographyDatasourceHandler.getGeographyResponse(assetName, authorization, start_time, end_time);
		List<ResponseObject> list = timeSeriesAqiParser.parse(datapointsResponse);
		OverallAqiResponse overallAqiResponse = aqiCalculations.calculateAqiMachine(list, Long.parseLong(start_time), Long.parseLong(end_time));
		return new ResponseEntity<Object>(overallAqiResponse, HttpStatus.OK);
	}

	@Autowired
	MachineSimulater machineSimulater;

	@GET
	@Path("/generatedArea")
	public ResponseEntity<Map<String, Object>> getGeneratedValueArea(@QueryParam("asset_name") String assetName) throws Throwable {
		return new ResponseEntity<Map<String, Object>>(machineSimulater.getGeneratedDataArea(assetName), HttpStatus.OK);
	}

	@GET
	@Path("/generatedMachine")
	public ResponseEntity<Map<String, Object>> getGeneratedValueMachine(@QueryParam("asset_name") String assetName) throws Throwable {
		return new ResponseEntity<Map<String, Object>>(machineSimulater.getGeneratedDataMachine(assetName), HttpStatus.OK);
	}

	@GET
	@Path("/generatedArea24Hrs")
	public ResponseEntity<Map<String, Object>> generatedArea24Hrs(@QueryParam("asset_name") String assetName) throws Throwable {
		return new ResponseEntity<Map<String, Object>>(machineSimulater.getGeneratedDataAreaLast24Hrs(assetName), HttpStatus.OK);
	}

	@GET
	@Path("/generatedArea20Sec")
	public ResponseEntity<Map<String, Object>> generatedArea20Hrs(@QueryParam("asset_name") String assetName) throws Throwable {
		return new ResponseEntity<Map<String, Object>>(machineSimulater.getGeneratedDataAreaLast20Sec(assetName), HttpStatus.OK);
	}

}
