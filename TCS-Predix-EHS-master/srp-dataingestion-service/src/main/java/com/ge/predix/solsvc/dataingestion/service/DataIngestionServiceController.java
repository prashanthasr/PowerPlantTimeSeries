package com.ge.predix.solsvc.dataingestion.service;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.asset.AssetTag;
import com.ge.predix.entity.asset.TagDatasource;
import com.ge.predix.solsvc.dataingestion.api.DataIngestionServiceAPI;
import com.ge.predix.solsvc.dataingestion.handler.DataIngestionHandler;

/**
 * 
 * @author predix -
 */
@RestController
@SuppressWarnings("nls")
public class DataIngestionServiceController
        implements DataIngestionServiceAPI
{
    private static Logger        log       = LoggerFactory.getLogger(DataIngestionServiceController.class);

    @Autowired
    private DataIngestionHandler dataIngestionHandler;

    /**
     * @return -
     */
    @RequestMapping(value="/ping",method=RequestMethod.GET)
	public String ping() {
		return "SUCCESS"; //$NON-NLS-1$
	}
    
    @Override
    @RequestMapping(value = "/ingestdata", method = RequestMethod.POST)
    public @ResponseBody String ingestFileData(
            @RequestHeader(value = "authorization", required = false) String authorization,
            @RequestParam("filename") String fileName, @RequestParam("clientId") String clientId,
            @RequestParam("tenantId") String tenantId, @RequestParam("file") MultipartFile file)
    {
        try
        {
            if ( !file.isEmpty() )
            {
                try
                {
                    byte[] bytes = file.getBytes();

                    // authorization =
                    // "Bearer eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIwMjNhNDIyZC1jYmY2LTQxZmQtYTQ4MC00NTcwMDE0YTVkYmQiLCJzdWIiOiJjNjE2MmQyOS03MjE0LTRmNTktOGRmNi0wY2UwNTY5ZGVkYzAiLCJzY29wZSI6WyJvcGVuaWQiXSwiY2xpZW50X2lkIjoicm1kX3JlZl9hcHAiLCJjaWQiOiJybWRfcmVmX2FwcCIsImF6cCI6InJtZF9yZWZfYXBwIiwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwidXNlcl9pZCI6ImM2MTYyZDI5LTcyMTQtNGY1OS04ZGY2LTBjZTA1NjlkZWRjMCIsInVzZXJfbmFtZSI6InJtZF91c2VyXzEiLCJlbWFpbCI6InJtZF91c2VyXzFAZXhhbXBsZS5jb20iLCJpYXQiOjE0Mjc5OTg0OTIsImV4cCI6MTQyODA0MTY5MiwiaXNzIjoiaHR0cHM6Ly91YWEtc3RhZ2luZy5udXJlZ28uY29tL29hdXRoL3Rva2VuIiwiYXVkIjpbInJtZF9yZWZfYXBwIiwib3BlbmlkIl0sIm9yaWdpbiI6InVhYSJ9.F0lgyBuvzOc0TDTMYkk2xsdGJWvD8PQNR-QrvwKmmV1_D2GPN5o0sh_w3ixYw1VMJlltbjJPIQMk0eZbuj-cR4hvCDM1sjT3X_NecxM3C9_Z6LalfpVs8QlTK_OitpjdNnVarOBTq5YT12mRXozZSB1iHC8ESV3ABl0rbMNLDPm7Ni97gpuOqCSa2xqHw8zMN3-cdESImDS6Gvvot30mRa1CbXhSmxVd0r--jami7UVPR7fHixEsrAOvgc5lyQigQuEMtfMxRLvNflyHF3F4TpHRpxo5owBdS9gJ1dJpmG3YIgNa1Q-p-l7D89mrhht6Ef9OB_n93Eu-R4Lmli4Ybg";
                    this.dataIngestionHandler.handleData( clientId, new String(bytes), authorization);
                    return "You successfully uploaded " + fileName + "!";
                }
                catch (Throwable e)
                {
                    log.error("Unable to Upload " + fileName, e);
                    return "You failed to upload " + fileName + " => " + e.getMessage();
                }
            }
            return "You failed to upload " + fileName + " because the file was empty.";
        }
        catch (Throwable e)
        {
            log.error("Unable to Upload " + fileName, e);
            return "Request failed. " + e.getMessage();
        }
    }

    @Override
    @RequestMapping(value = "/saveTimeSeriesData", method = RequestMethod.POST)
    public @ResponseBody String saveTimeSeriesData(
            @RequestHeader(value = "authorization", required = false) String authorization,
            @RequestParam("clientId") String clientId,
            @RequestParam("content") String content)
    {

        try
        {
        	log.debug("Content : "+content);
            this.dataIngestionHandler.handleData(clientId, content, authorization);
            return "You successfully posted data";
        }
        catch (Throwable e)
        {
            log.error("Unable to Save " + content, e);
            throw new RuntimeException(e);
        }

    }

    @Override
    @RequestMapping(value = "/ingestdata", method = RequestMethod.GET)
    public @ResponseBody String handleRequest()
    {
        try
        {
            return this.dataIngestionHandler.listAssets();
        }
        catch (Throwable e)
        {
            log.error("Failure in /ingestdata GET ", e);
            return "Request failed. " + e.getMessage();
        }

    }

    /**
     * @return -
     */
    @RequestMapping(value = "/retrieveasset", method = RequestMethod.GET)
    public @ResponseBody String retrieveAsset()
    {
        try
        {
            return this.dataIngestionHandler.retrieveAsset();
        }
        catch (Throwable e)
        {
            log.error("Failure in /retrieveasset GET ", e);
            return "Request failed. " + e.getMessage();
        }

    }

    /**
     * @param asset -
     * @return -
     */
    protected String getTimeSeriesTag(Asset asset)
    {
        @SuppressWarnings("unchecked")
		LinkedHashMap<String, AssetTag> tags = asset.getAssetTag();
        if ( tags != null )
        {
            for (Entry<String, AssetTag> entry : tags.entrySet())
            {
                AssetTag assetTag = entry.getValue();
                TagDatasource dataSource = assetTag.getTagDatasource();
                if ( dataSource != null && !dataSource.getNodeName().isEmpty() )
                {
                    return assetTag.getSourceTagId();
                }
            }
        }
        return null;
    }

	@Override
	@CrossOriginResourceSharing(allowOrigins = "https://powerplant-rmd.run.aws-usw02-pr.ice.predix.io") 
	@RequestMapping(value = "/gettimeseriesdata", method = RequestMethod.GET)
	public String getTimeSeriesData(@HeaderParam(value = "authorization")String authorization,@QueryParam("content")String content) {
		try
        {
            return this.dataIngestionHandler.getTimeSeriesData(authorization,content);
        }
        catch (Throwable e)
        {
            log.error("Failure in /retrieveasset GET ", e);
            return "Request failed. " + e.getMessage();
        }
	}

	@Override
	@RequestMapping(value = "/lastfivemindata/", method = RequestMethod.GET)
	public String getLastFiveMinData (@HeaderParam(value = "authorization")String authorization,@QueryParam("assetSerialId")String assetSerialId) {
		return this.dataIngestionHandler.getLastFiveMinutesData(authorization, assetSerialId);
	}

	@Override
	@RequestMapping(value = "/getdatafortimeframe/", method = RequestMethod.GET)
	public String getTimeSeriesDataForTimePeriod(@HeaderParam(value = "authorization")String authorization, @QueryParam("assetSerialId") String assetSerialId,@QueryParam("timeStampFrom") Long timeStampFrom,
			@QueryParam("timeStampTo")Long timeStampTo) {
		return this.dataIngestionHandler.getDataForTimeFrame(authorization, assetSerialId, timeStampFrom, timeStampTo);
	}

		
	
	
}
