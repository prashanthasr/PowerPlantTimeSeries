package com.ge.predix.solsvc.dataingestion.api;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author predix -
 */
public interface DataIngestionServiceAPI
{
    /**
     * @param authorization -
     * @param fileName -
     * @param clientId -
     * @param tenantId -
     * @param file -
     * @return -
     */
    public @ResponseBody String ingestFileData(String authorization, String fileName, String clientId,
            String tenantId, MultipartFile file);

    /**
     * @return -
     */
    public @ResponseBody String handleRequest();

    /**
     * @param authorization -
     * @param clientId -
     * @param tenantId -
     * @param content -
     * @return -
     */
    public @ResponseBody String saveTimeSeriesData(
            @RequestHeader(value = "authorization", required = false) String authorization,
            @RequestParam("clientId") String clientId, 
            @RequestParam("content") String content);
    
    /**
     * 
     * @param authorization
     * @param clientId
     * @param content
     * @return
     */
    public @ResponseBody String getTimeSeriesData(
    		@RequestHeader(value = "authorization", required = false) String authorization,
    		String content
    		);
    
    
    public @ResponseBody String getLastOneHourData(
    		@RequestHeader(value = "authorization", required = false) String authorization,
    		@RequestParam(value="assetSerialId") String assetSerialId);
}
