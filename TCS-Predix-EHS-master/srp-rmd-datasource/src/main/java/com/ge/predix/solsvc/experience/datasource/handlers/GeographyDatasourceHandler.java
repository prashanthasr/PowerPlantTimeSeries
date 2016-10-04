package com.ge.predix.solsvc.experience.datasource.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Group;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.experience.datasource.handlers.HygieneDatasourceHandler.MyGroup;
import com.ge.predix.solsvc.experience.datasource.handlers.utils.Constants;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesRestConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;

@Component
public class GeographyDatasourceHandler {
	@Autowired
	private TimeseriesFactory timeseriesFactory;

	@Autowired
	private TimeseriesRestConfig timeseriesRestConfig;

	@Autowired
	protected RestClient restClient;

	class MyGroup extends Group {
		private List<Object> attributes = new ArrayList<>();

		public List<Object> getAttributes() {
			return attributes;
		}

		public void setAttributes(List<Object> attributes) {
			this.attributes = attributes;
		}

		@Override
		public void setValues(List<Object> values) {
			super.setValues(null);
		}

		@Override
		public List<Object> getValues() {
			return null;
		}
	}

	public DatapointsResponse getGeographyResponse(String id, String authorization, String start_time, String end_time) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();

		List<Header> headers = new ArrayList<Header>();
		restClient.addSecureTokenToHeaders(headers, authorization);
		restClient.addZoneToHeaders(headers, timeseriesRestConfig.getZoneId());

		DatapointsQuery datapointsQuery = new DatapointsQuery();
		datapointsQuery.setStart(Long.parseLong(start_time));
		datapointsQuery.setEnd(Long.parseLong(end_time));

		Tag tag = new Tag();

		List<Group> groups = new ArrayList<>();
		MyGroup group = new MyGroup();
		group.setName("attribute");
		//String[] values = { "NO2", "PM2_5", "SO2" };

		String[] values = { "PM2_5", "PB", "O3", "CO2", "SO2", "NH3", "PM10" };
		group.setAttributes(Arrays.asList(values));
		groups.add(group);
		tag.setGroups(groups);
		tag.setName(id);
		tag.setLimit(10);
		//tag.setOrder("desc");

		List<Tag> tags = new ArrayList<>();
		tags.add(tag);
		datapointsQuery.setTags(tags);

		System.out.println("Query : "+objectMapper.writeValueAsString(datapointsQuery));

		DatapointsResponse response = timeseriesFactory.queryForDatapoints(timeseriesRestConfig.getBaseUrl(), datapointsQuery, headers);
		response.setStart(Double.parseDouble(start_time));
		response.setEnd(Double.parseDouble(end_time));

		System.out.println("Response : "+objectMapper.writeValueAsString(response));

		return response;
	}
	
	public DatapointsResponse getGeographyResponse(String authorization, String start_time, String end_time) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();

		List<Header> headers = new ArrayList<Header>();
		restClient.addSecureTokenToHeaders(headers, authorization);
		restClient.addZoneToHeaders(headers, timeseriesRestConfig.getZoneId());

		DatapointsQuery datapointsQuery = new DatapointsQuery();
		datapointsQuery.setStart(Long.parseLong(start_time));
		datapointsQuery.setEnd(Long.parseLong(end_time));

		

		List<Tag> tags = new ArrayList<>();
		for (int i = 0; i < Constants.aqiAreas.length; i++) {
			String[] values = { "PM2_5", "PB", "O3", "CO2", "SO2", "NH3", "PM10" };
			List<Group> groups = new ArrayList<>();
			MyGroup group = new MyGroup();
			group.setName("attribute");
			group.setAttributes(Arrays.asList(values));
			groups.add(group);
			Tag tag = new Tag();
			tag.setGroups(groups);
			tag.setName(Constants.aqiAreas[i]);
			tag.setLimit(10);
			tags.add(tag);
		}
		datapointsQuery.setTags(tags);

		System.out.println("Query : " + objectMapper.writeValueAsString(datapointsQuery));
		
		DatapointsResponse response = timeseriesFactory.queryForDatapoints(timeseriesRestConfig.getBaseUrl(), datapointsQuery, headers);
		response.setStart(Double.parseDouble(start_time));
		response.setEnd(Double.parseDouble(end_time));

		System.out.println("Response : " + objectMapper.writeValueAsString(response));

		return response;
	}
	
	
}
