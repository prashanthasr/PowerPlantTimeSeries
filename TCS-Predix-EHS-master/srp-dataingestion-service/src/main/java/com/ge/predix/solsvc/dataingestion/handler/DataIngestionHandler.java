package com.ge.predix.solsvc.dataingestion.handler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.asset.Asset;

/**
 * 
 * @author predix -
 */
@Component
public class DataIngestionHandler
{
    private static Logger          log = Logger.getLogger(DataIngestionHandler.class);
    @Autowired
    private TimeSeriesDataIngestionHandler timeSeriesDataIngestionHandler;

    /**
     * 
     */
    @Autowired
    private AssetDataHandler     assetDataHandler;

    /**
     *  -
     */
    @SuppressWarnings("nls")
    @PostConstruct
    public void intilizeDataIngestionHandler()
    {
        log.info("*******************DataIngestionHandler Initialization complete*********************");
    }

    /**
     * @param tenantId -
     * @param controllerId -
     * @param data -
     * @param authorization -
     * @return -
     */
    @SuppressWarnings("nls")
    public String handleData(String controllerId, String data, String authorization)
    {
        this.timeSeriesDataIngestionHandler.handleData(data, authorization);
        return "SUCCESS";
    }
    
    public String getTimeSeriesData(String authorization, String content)
    {
    	return this.timeSeriesDataIngestionHandler.getTimeSeriesData(authorization,content);
    }
    
    
    public String getLastFiveMinutesData(String authorization, String assetSerialId)
    {
    	return this.timeSeriesDataIngestionHandler.getLastFiveMinutesData(authorization,assetSerialId);
    }
    
    public String getDataForTimeFrame(String authorization, String assetSerialId,Long timeFrom, Long timeTo)
    {
    	return this.timeSeriesDataIngestionHandler.getDataForTimeFrame(authorization, assetSerialId,timeFrom, timeTo);
    }

    /**
     * @return -
     */
    public String listAssets()
    {
        List<Asset> assets = this.assetDataHandler.getAllAssets();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try
        {
            mapper.writeValue(writer, assets);
        }
        catch (JsonGenerationException e)
        {
            throw new RuntimeException(e);
        }
        catch (JsonMappingException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public String retrieveAsset()
    {
        Asset asset = this.assetDataHandler.retrieveAsset(null, "assetId", "/asset/676",
                null);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try
        {
            mapper.writeValue(writer, asset);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
}
