package com.ge.predix.solsvc.dataingestion.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesRestConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;

/**
 * 
 * @author predix -
 */
@Component
public class TimeSeriesDataIngestionHandler extends BaseFactory {
	private static Logger log = Logger.getLogger(TimeSeriesDataIngestionHandler.class);
	@Autowired
	private TimeseriesFactory timeSeriesFactory;


	@Autowired
	private JsonMapper jsonMapper;

	@Autowired
	private TimeseriesRestConfig timeseriesRestConfig;

	@Autowired
	Environment evn;

	/**
	 * -
	 */
	@SuppressWarnings("nls")
	@PostConstruct
	public void intilizeDataIngestionHandler() {
		log.info("*******************TimeSeriesDataIngestionHandler Initialization complete*********************");
	}

	/**
	 * @param data
	 *            -
	 * @param authorization
	 *            -
	 */
	@SuppressWarnings("nls")
	public void handleData(String data, String authorization) {
		log.info("Data from Simulator : " + data);
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers, this.timeseriesRestConfig.getZoneId());
		headers.add(new BasicHeader("Origin", "http://localhost"));
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.setSerializationInclusion(Include.NON_NULL);
			
			DatapointsIngestion dpIngestion = (DatapointsIngestion) mapper.readValue(data, new com.fasterxml.jackson.core.type.TypeReference<DatapointsIngestion>() {});
			
			log.info("TimeSeries JSON : " + this.jsonMapper.toJson(dpIngestion));
			if (dpIngestion.getBody() != null && dpIngestion.getBody().size() > 0) {
				log.info("Posted Data to Predix Websocket Server");
				this.timeSeriesFactory.createConnectionToTimeseriesWebsocket(headers);
				this.timeSeriesFactory.postDataToTimeseriesWebsocket(dpIngestion, headers);
				this.timeSeriesFactory.closeConnectionToTimeseriesWebsocket();
				log.info("Posted Data to Timeseries");
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	public String getTimeSeriesData(String authorization, String content)
	{
		return triggerTimeSeriesRequest(authorization,content);
	}
	
	public String getLastFiveMinutesData(String authorization, String assetSerialId)
	{
		
		String content ="{ 'start':'5mi-ago' , 'tags': [ {'name': 'asset-"+assetSerialId+":voltage','order': 'desc'},{'name': 'asset-"+assetSerialId+":ampere','order': 'desc'} ]}";
		
		return triggerTimeSeriesRequest(authorization,content);
			
	}
	
	public String getDataForTimeFrame(String authorization, String assetSerialId,Long timeFrom, Long timeTo)
	{
		
		String content ="{ 'start':"+timeFrom +",'end':"+ timeTo +", 'tags': [ {'name': 'asset-"+assetSerialId+":voltage'},{'name': 'asset-"+assetSerialId+":ampere'} ]}";
		
		return triggerTimeSeriesRequest(authorization,content);
	
	}
	
	private String triggerTimeSeriesRequest(String authorization,String content)
	{
		List<Header> headers = null;
		if (StringUtils.isEmpty(authorization)) {
			System.out.println("getting Headers");
			headers = this.restClient.getSecureTokenForClientId();
			System.out.println("headers : " + headers);
		} else {
			headers = new ArrayList<Header>();
			this.restClient.addSecureTokenToHeaders(headers, authorization);
		}
		this.restClient.addZoneToHeaders(headers, this.timeseriesRestConfig.getZoneId()); 
		
		final String uri = "https://time-series-store-predix.run.aws-usw02-pr.ice.predix.io/v1/datapoints";
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		
		setLocalProxy(requestFactory);
		
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers1.add(headers.get(0).getName(), headers.get(0).getValue());
		headers1.add("predix-zone-id", this.timeseriesRestConfig.getZoneId());
		headers1.add("content", content);
		
		HttpEntity<String> entity = new HttpEntity<String>(content,headers1);
		

		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		System.out.println("data >>> " + result.getBody());
		
		return result.getBody();
	}
	
	private void setLocalProxy(SimpleClientHttpRequestFactory clientProxy)
	{
		//Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("pitc-zscaler-aspac-bangalore3pr.proxy.corporate.ge.com", 443));
		//clientProxy.setProxy(proxy);
	}
	
	


	/*private long converLocalTimeToUtcTime(long timeSinceLocalEpoch) {
		return timeSinceLocalEpoch + getLocalToUtcDelta();
	}

	private long getLocalToUtcDelta() {
		Calendar local = Calendar.getInstance();
		local.clear();
		local.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
		return local.getTimeInMillis();
	}*/

	
}
