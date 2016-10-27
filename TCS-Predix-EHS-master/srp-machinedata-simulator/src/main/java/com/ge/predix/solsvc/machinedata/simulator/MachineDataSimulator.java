package com.ge.predix.solsvc.machinedata.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.machinedata.simulator.config.Constants;
import com.ge.predix.solsvc.machinedata.simulator.vo.AQIAttributesVO;
import com.ge.predix.solsvc.machinedata.simulator.vo.AQIBody;
import com.ge.predix.solsvc.machinedata.simulator.vo.AQIObjectVO;
import com.ge.predix.solsvc.machinedata.simulator.vo.HygeineAtrributesVO;
import com.ge.predix.solsvc.machinedata.simulator.vo.HygeineBody;
import com.ge.predix.solsvc.machinedata.simulator.vo.HygeineObjectVO;
import com.ge.predix.solsvc.machinedata.simulator.vo.PowerPlantAssetAttribute;

/**
 * 
 * @author predix -
 */
@Component
public class MachineDataSimulator {
    /**
     * 
     */
    static final Logger log = LoggerFactory.getLogger(MachineDataSimulator.class);
    private ObjectMapper mapper = new ObjectMapper();
    
    private int[] itemsVoltageValues = new int[]{230,100,250,113,145,300,110,120,220,144};

    private Random rand = new Random();

    public MachineDataSimulator() {
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        // mapper.configure(SerializationFeature.INDENT_OUTPUT,true);
        mapper.setSerializationInclusion(Include.NON_NULL);

    }

    /**
     * 
     */
    @Autowired
    ApplicationProperties applicationProperties;
 

    /**
     * -
     */
    @Scheduled(fixedDelayString = "${dataingestion.sleepTimeMillis}")
    public void run() {

        try {
        	
        	generateAndPushAssetDataForVoltage();
        	generateAndPushAssetDataForAmpere();
        	//generateAndPushAssetDataForStatus();
        	     

        } catch (Throwable e) {
            log.error("unable to run Machine DataSimulator Thread", e); //$NON-NLS-1$
        }
    }
    
    private String[] voltageAssetTags = new String[]{"asset-100:voltage",
    										"asset-101:voltage",
    										"asset-102:voltage",
    										"asset-103:voltage",
    										"asset-104:voltage"
    										};
    
    private String[] ampereAssetTags = new String[]{"asset-100:ampere",
			"asset-101:ampere",
			"asset-102:ampere",
			"asset-103:ampere",
			"asset-104:ampere"
			};
    
    private String getRandamVoltageAssetTag()
    {
    	return voltageAssetTags[rand.nextInt(voltageAssetTags.length)];
    }
    
    private String getRandamAmpereAssetTag()
    {
    	return ampereAssetTags[rand.nextInt(ampereAssetTags.length)];
    }
    
    private String generateAndPushAssetDataForVoltage() throws JsonGenerationException, JsonMappingException, IOException {
    	Long currentTimeMillis = System.currentTimeMillis();
    	
    	AQIObjectVO aqiObjVO = new AQIObjectVO();
    	
    	List<AQIBody> aqiBodyList = new ArrayList<>();
    	
    	AQIBody assetAreaBodyForVolts = getRandomPowerPlantData(currentTimeMillis, getRandamVoltageAssetTag(),"V");
    	
    	aqiBodyList.add(assetAreaBodyForVolts);	
    	
    	aqiObjVO.setBody(aqiBodyList);
    	aqiObjVO.setMessageId(currentTimeMillis);
		
				
		DatapointsIngestion dataPtIngest = createAQIDataIngestionRequest(aqiObjVO);
		
		StringWriter writer = new StringWriter();

		mapper.writeValue(writer, dataPtIngest);
        System.out.println("Final JSON sending to saveTimeSeries >> "+writer.toString());
		return postData(writer.toString());
	}
    
    private String generateAndPushAssetDataForAmpere() throws JsonGenerationException, JsonMappingException, IOException {
    	Long currentTimeMillis = System.currentTimeMillis();
    	
    	AQIObjectVO aqiObjVO = new AQIObjectVO();
    	
    	List<AQIBody> aqiBodyList = new ArrayList<>();
    	
    	//AQIBody assetAreaBodyForVolts = getRandomPowerPlantData(currentTimeMillis, getRandamAssetTag(),"V");
    	AQIBody assetAreaBodyForCurrent = getRandomPowerPlantData(currentTimeMillis, getRandamAmpereAssetTag(),"A");
    	//AQIBody assetAreaBodyForStatus = getRandomPowerPlantData(currentTimeMillis, getRandamAssetTag(),"O");
    	
    	//AQIBody smtAreaBody = getRandomAQIDataVO(currentTimeMillis, getRandamAssetTag(), true); 
    	//AQIBody prdGrndFlrBody = getRandomAQIDataVO(currentTimeMillis, "Production Ground Floor", true);
    	//AQIBody nearSoldMacBody = getRandomAQIDataVO(currentTimeMillis, "Near Soldering Machine", true);
    	
    	//aqiBodyList.add(assetAreaBodyForVolts);	
    	aqiBodyList.add(assetAreaBodyForCurrent);
    	//aqiBodyList.add(assetAreaBodyForStatus);
    	
    	aqiObjVO.setBody(aqiBodyList);
    	aqiObjVO.setMessageId(currentTimeMillis);
		
				
		DatapointsIngestion dataPtIngest = createAQIDataIngestionRequest(aqiObjVO);
		
		StringWriter writer = new StringWriter();

		mapper.writeValue(writer, dataPtIngest);
        System.out.println("Final JSON sending to saveTimeSeries >> "+writer.toString());
		return postData(writer.toString());
       // return writer.toString();
		
	}
    
    private String generateAndPushAssetDataForStatus() throws JsonGenerationException, JsonMappingException, IOException {
    	Long currentTimeMillis = System.currentTimeMillis();
    	
    	AQIObjectVO aqiObjVO = new AQIObjectVO();
    	
    	List<AQIBody> aqiBodyList = new ArrayList<>();
    	
    	//AQIBody assetAreaBodyForVolts = getRandomPowerPlantData(currentTimeMillis, getRandamAssetTag(),"V");
    	//AQIBody assetAreaBodyForCurrent = getRandomPowerPlantData(currentTimeMillis, getRandamAssetTag(),"A");
    	//AQIBody assetAreaBodyForStatus = getRandomPowerPlantData(currentTimeMillis, getRandamAssetTag(),"O");
    	
    	//AQIBody smtAreaBody = getRandomAQIDataVO(currentTimeMillis, getRandamAssetTag(), true); 
    	//AQIBody prdGrndFlrBody = getRandomAQIDataVO(currentTimeMillis, "Production Ground Floor", true);
    	//AQIBody nearSoldMacBody = getRandomAQIDataVO(currentTimeMillis, "Near Soldering Machine", true);
    	
    	//aqiBodyList.add(assetAreaBodyForVolts);	
    	//aqiBodyList.add(assetAreaBodyForCurrent);
    	//aqiBodyList.add(assetAreaBodyForStatus);
    	
    	aqiObjVO.setBody(aqiBodyList);
    	aqiObjVO.setMessageId(currentTimeMillis);
		
				
		DatapointsIngestion dataPtIngest = createAQIDataIngestionRequest(aqiObjVO);
		
		StringWriter writer = new StringWriter();

		mapper.writeValue(writer, dataPtIngest);
        System.out.println("Final JSON sending to saveTimeSeries >> "+writer.toString());
		return postData(writer.toString());
       // return writer.toString();
		
	}

    private String generateAndPushRandomAreaData() throws JsonGenerationException, JsonMappingException, IOException {
    	Long currentTimeMillis = System.currentTimeMillis();
    	
    	AQIObjectVO aqiObjVO = new AQIObjectVO();
    	
    	List<AQIBody> aqiBodyList = new ArrayList<>();
    	
    	AQIBody smtAreaBody = getRandomAQIDataVO(currentTimeMillis, "asset:676", true);
    	//AQIBody prdGrndFlrBody = getRandomAQIDataVO(currentTimeMillis, "Production Ground Floor", true);
    	//AQIBody nearSoldMacBody = getRandomAQIDataVO(currentTimeMillis, "Near Soldering Machine", true);
    	
    	aqiBodyList.add(smtAreaBody);
    	//aqiBodyList.add(prdGrndFlrBody);
    	//aqiBodyList.add(nearSoldMacBody);
    	
    	aqiObjVO.setBody(aqiBodyList);
    	aqiObjVO.setMessageId(currentTimeMillis);
		
				
		DatapointsIngestion dataPtIngest = createAQIDataIngestionRequest(aqiObjVO);
		
		StringWriter writer = new StringWriter();

		mapper.writeValue(writer, dataPtIngest);
        System.out.println("Final JSON sending to saveTimeSeries >> "+writer.toString());
		return postData(writer.toString());
       // return writer.toString();
		
	}

    private String generateAndPushRandomMachineData() throws JsonGenerationException, JsonMappingException, IOException {
    	Long currentTimeMillis = System.currentTimeMillis();
    	
    	AQIObjectVO aqiObjVO = new AQIObjectVO();
    	
    	List<AQIBody> aqiBodyList = new ArrayList<>();
    	
    	AQIBody hellMchBody = getRandomAQIDataVO(currentTimeMillis, "Heller-Machine", false);
    	AQIBody solMchBody = getRandomAQIDataVO(currentTimeMillis, "Soltech-Machine", false);
    	AQIBody refOvnMchBody = getRandomAQIDataVO(currentTimeMillis, "Reflow-Ovan", false);
    	AQIBody waveSoldMchBody = getRandomAQIDataVO(currentTimeMillis, "Wave-Soldering-Machine", false);
    	
    	aqiBodyList.add(hellMchBody);
    	aqiBodyList.add(solMchBody);
    	aqiBodyList.add(refOvnMchBody);
    	aqiBodyList.add(waveSoldMchBody);
    	
    	aqiObjVO.setBody(aqiBodyList);
    	aqiObjVO.setMessageId(currentTimeMillis);
		
			
		DatapointsIngestion dataPtIngest = createAQIDataIngestionRequest(aqiObjVO);
		
		StringWriter writer = new StringWriter();

		mapper.writeValue(writer, dataPtIngest);
        System.out.println("Final JSON sending to saveTimeSeries >> "+writer.toString());
		return postData(writer.toString());
       // return writer.toString();
		
	}
	

	private String generateAndPushRandomHygieneData() throws JsonGenerationException, JsonMappingException, IOException {

        Long currentTimeMillis = System.currentTimeMillis();
        
        HygeineObjectVO hygObjVO = new HygeineObjectVO();
        
        List<HygeineBody> hygBodyList = new ArrayList<>();
        
        
        HygeineBody smtLine1HygBodyVo = createHygieneBodyVO(currentTimeMillis, "SMT Line 1");
        HygeineBody smtLine2HygBodyVo = createHygieneBodyVO(currentTimeMillis, "SMT Line 2");
        HygeineBody prdGrndFlrHygBodyVo = createHygieneBodyVO(currentTimeMillis, "Hygiene Production Ground Floor");
        
        hygBodyList.add(smtLine1HygBodyVo);
        hygBodyList.add(smtLine2HygBodyVo);
        hygBodyList.add(prdGrndFlrHygBodyVo);
        
        hygObjVO.setBody(hygBodyList);
        hygObjVO.setMessageId(currentTimeMillis);
        
        
        DatapointsIngestion dataPtIngest = createHygieneDataIngestionRequest(hygObjVO);
        StringWriter writer = new StringWriter();

        mapper.writeValue(writer, dataPtIngest);
        System.out.println("Final JSON sending to saveTimeSeries >> "+writer.toString());
        return postData(writer.toString());
       // return writer.toString();
    
    }

    private HygeineBody createHygieneBodyVO(Long currentTimeMillis, String bodyName) {
        HygeineBody hygBodyVo = new HygeineBody();
        hygBodyVo.setName(bodyName);        
        
        ArrayList<Long> datapoint = new ArrayList<Long>();
        datapoint.add(currentTimeMillis);
        datapoint.add(10l);
        datapoint.add(1l);
        
        ArrayList<ArrayList<Long>> datapoints = new ArrayList<>();
        datapoints.add(datapoint);
        hygBodyVo.setDatapoints(datapoints);
        
        
        HygeineAtrributesVO hygAttVO = new HygeineAtrributesVO();
        hygAttVO.setHumidity(getHygeineValues(Constants.Hygiene.HUMIDITY));
        hygAttVO.setNoise(getHygeineValues(Constants.Hygiene.NOISE));
        hygAttVO.setTemperature(getHygeineValues(Constants.Hygiene.TEMPERATURE));
        
        
        hygBodyVo.setAttributes(hygAttVO);
        return hygBodyVo;
    }
    
   

    /**
     * @return String Response string
     * @throws Exception
     *             -
     */
    public String runTest() throws Exception {
        List<JSONData> list = generateMockDataMap_RT();

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();

        mapper.writeValue(writer, list);
        return postData(writer.toString());

    }

    /*
     * private MessageObject getMessageObject(EHSObjectVO message) throws
     * JsonProcessingException { MessageObject messageObject = new
     * MessageObject(); Long currentTimeMillis = System.currentTimeMillis();
     * ArrayList<Long> datapoint = new ArrayList<>();
     * datapoint.add(currentTimeMillis); datapoint.add(10l); datapoint.add(1l);
     * messageObject.setMessageId(currentTimeMillis); for (AssetBody ehsArea :
     * message.getBody()) { Body body = new Body();
     * 
     * body.setO3(getValues(Constants.Parameter.O3));
     * body.setNH3(getValues(Constants.Parameter.NH3));
     * body.setNO2(getValues(Constants.Parameter.NO2));
     * body.setPB(getValues(Constants.Parameter.Pb));
     * body.setCO2(getValues(Constants.Parameter.CO));
     * body.setSO2(getValues(Constants.Parameter.SO2));
     * body.setPM2_5(getValues(Constants.Parameter.PM2_5));
     * body.setPM10(getValues(Constants.Parameter.PM10));
     * 
     * body.setName(ehsArea.getName()); body.getDatapoints().add(datapoint);
     * messageObject.getBody().add(body); } return messageObject; }
     */


	@SuppressWarnings("unchecked")
	private DatapointsIngestion createAQIDataIngestionRequest(AQIObjectVO inputObj) throws com.fasterxml.jackson.core.JsonParseException, com.fasterxml.jackson.databind.JsonMappingException, IOException {

		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		// dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		dpIngestion.setMessageId(String.valueOf(calendar.getTimeInMillis()));
		List<Body> bodies = new ArrayList<Body>();

		//
		List<AQIBody> aqiBodies = inputObj.getBody();
		for (AQIBody aqiBody : aqiBodies) {
			PowerPlantAssetAttribute aqiAttributes = aqiBody.getAttributes();
			ArrayList<ArrayList<Object>> aqiDatapoints = aqiBody.getDatapoints();
			String aqiBodyName = aqiBody.getName();

			// Create DataIngestion Object
			Body body = new Body();
			body.setName(aqiBodyName);

			List<Object> datapoints = new ArrayList<Object>();
			List<Object> assetDatapoint = new ArrayList<Object>();
			assetDatapoint.add(String.valueOf(calendar.getTimeInMillis()));
			assetDatapoint.add(aqiDatapoints.get(0).get(1));
			//assetDatapoint.add(aqiDatapoints.get(0).get(2));

			datapoints.add(assetDatapoint);

			body.setDatapoints(datapoints);

			com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
			map.put("AssetSerialId", aqiAttributes.getAssetSerialId());
			map.put("Measurement", aqiAttributes.getMeasurement());
			map.put("SensorId", aqiAttributes.getSensorId());
			map.put("Type", aqiAttributes.getType());
			
			body.setAttributes(map);
			bodies.add(body);

		}

		dpIngestion.setBody(bodies);

		return dpIngestion;

	}

	// creating dataingestion connection
	@SuppressWarnings("unchecked")
	private DatapointsIngestion createHygieneDataIngestionRequest(HygeineObjectVO inputObj) throws com.fasterxml.jackson.core.JsonParseException, com.fasterxml.jackson.databind.JsonMappingException, IOException {

		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		// dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		dpIngestion.setMessageId(String.valueOf(calendar.getTimeInMillis()));
		List<Body> bodies = new ArrayList<Body>();

		//
		List<HygeineBody> hygBodies = inputObj.getBody();
		for (HygeineBody hygBody : hygBodies) {
			HygeineAtrributesVO hygAttributes = hygBody.getAttributes();
			ArrayList<ArrayList<Long>> aqiDatapoints = hygBody.getDatapoints();
			String aqiBodyName = hygBody.getName();

			// Create DataIngestion Object
			Body body = new Body();
			body.setName(aqiBodyName);

			List<Object> datapoints = new ArrayList<Object>();
			List<Object> assetDatapoint = new ArrayList<Object>();
			assetDatapoint.add(String.valueOf(calendar.getTimeInMillis()));
			assetDatapoint.add(aqiDatapoints.get(0).get(1));
			assetDatapoint.add(aqiDatapoints.get(0).get(2));

			datapoints.add(assetDatapoint);

			body.setDatapoints(datapoints);

			com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
			map.put("humidity", hygAttributes.getHumidity());
			map.put("noise", hygAttributes.getNoise());
			map.put("temperature", hygAttributes.getTemperature());
			
			body.setAttributes(map);
			bodies.add(body);

		}

		dpIngestion.setBody(bodies);

		return dpIngestion;

	}
	
	private int[] amperes = new int[]{12,6,36,15,0,};
	private int[] onOffArray = new int[]{1,0};
	
	public int getRamdAmpsArray()
	{
		return amperes[rand.nextInt(amperes.length)];
	}
    public int getRandVoltageArrayElement(){
        return itemsVoltageValues[rand.nextInt(itemsVoltageValues.length)];
    }
    
    public int getOnOffSensorData()
    {
    	return onOffArray[rand.nextInt(onOffArray.length)];
    }
       
    private String[] sesnorIds = new String[]{"Sensor123","Sensor234","Sensor678"};
    
   
    
    private String getSensorId()
    {
    	return sesnorIds[rand.nextInt(sesnorIds.length)];
    }
    
	 private AQIBody getRandomPowerPlantData(Long currentTimeMillis, String bodyName,String dataPointType) throws JsonProcessingException {
	      
	        AQIBody aqiBodyVo = new AQIBody();
	        
	        aqiBodyVo.setName(bodyName);
	        
	        ArrayList<Object> datapoint = new ArrayList<>();
	        datapoint.add(currentTimeMillis);
	        switch(dataPointType)
	        {
		        case "V":{
		        	datapoint.add((Object) (getRandVoltageArrayElement()));
		        	break;
		        }
		        case "A":{
		        	datapoint.add((Object) (getRamdAmpsArray()));
		        	break;
		        }
		        /*case "O":{
		        	datapoint.add((Object) ((getOnOffSensorData()==0)?"OFF":"ON"));
		        	break;
		        }*/
	        }
	        
	        
	        
	        ArrayList<ArrayList<Object>> datapoints = new ArrayList<>();
	        datapoints.add(datapoint);
	        aqiBodyVo.setDatapoints(datapoints);
	        
	        PowerPlantAssetAttribute ppAssetAttr = new PowerPlantAssetAttribute();
	        
	        String[] assetNameStr = bodyName.split(":");
            
	        ppAssetAttr.setAssetSerialId(assetNameStr[0]);
	        ppAssetAttr.setType("SolarPanel");
	        switch(dataPointType)
	        {
		        case "V":{
		        	ppAssetAttr.setMeasurement("Voltage");
		        	break;
		        }
		        case "A":{
		        	ppAssetAttr.setMeasurement("Ampere");	
		        	break;
		        }
		        case "O":{
		        	ppAssetAttr.setMeasurement("Status");
		        	break;
		        }
	        }
	        
	        ppAssetAttr.setSensorId(getSensorId());
		
	        aqiBodyVo.setAttributes(ppAssetAttr);
	        
	        return aqiBodyVo;
	    }
    
    private AQIBody getRandomAQIDataVO(Long currentTimeMillis, String bodyName, boolean areaRequest) throws JsonProcessingException {
      
        AQIBody aqiBodyVo = new AQIBody();
        
        aqiBodyVo.setName(bodyName);
        
        ArrayList<Object> datapoint = new ArrayList<>();
        datapoint.add(currentTimeMillis);
        datapoint.add(10l);
        datapoint.add(1l);
        ArrayList<ArrayList<Object>> datapoints = new ArrayList<>();
        datapoints.add(datapoint);
        aqiBodyVo.setDatapoints(datapoints);
       
        AQIAttributesVO aqiAttVO = new AQIAttributesVO();
        
        if(areaRequest){
        	aqiAttVO.setCO(getValues(Constants.Parameter.CO));
        	aqiAttVO.setO3(getValues(Constants.Parameter.O3));
        	aqiAttVO.setNH3(getValues(Constants.Parameter.NH3));
        	aqiAttVO.setPB(getValues(Constants.Parameter.PB));
        	aqiAttVO.setPM10(getValues(Constants.Parameter.PM10));
        }
    	
    	aqiAttVO.setNO2(getValues(Constants.Parameter.NO2));    	
    	aqiAttVO.setPM2_5(getValues(Constants.Parameter.PM2_5));
    	aqiAttVO.setSO2(getValues(Constants.Parameter.SO2));
    	
       // aqiBodyVo.setAttributes(aqiAttVO);
        
        return aqiBodyVo;
    }

    
    private Double getHygeineValues(Constants.Hygiene hygiene) {
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
		case CO:
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
		case CO:
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
   
   /* public String readEHSDataAndGenerateRandomJSON(String jsonFileName) throws Exception {
        // TODO: Read data from Sensor by passing current timestamp

        //
        // Generate JSON from EHS Data
        AQIObjectVO ehsObj = mapperService.parse(jsonFileName);
        Long currentTimeMillis = System.currentTimeMillis();
        
        AQIObjectVO updatedEHSObject = getRandomEHSObject(ehsObj, currentTimeMillis);
        
        List<AQIBody> bodyList = updatedEHSObject.getBody();
        System.out.println("Body List: \n" + bodyList);
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("Random EHSObject Data: \n" + updatedEHSObject);

        System.out.println("--------------------------------------------------");
        System.out.println();
        // ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();

        mapper.writeValue(writer, bodyList);
        // String jsonString = mapper.writeValueAsString(bodyList);
        // System.out.println("jsonString >> "+jsonString);
        return postData(writer.toString());
        
        Long currentTimeMillis = System.currentTimeMillis();
        
        EHSObjectVO ehsObj = mapperService.parse(jsonFileName);
        EHSObjectVO updatedEHSObject = getEHSObject(ehsObj, currentTimeMillis);

        ObjectMapper mapper = new ObjectMapper();
        List<AssetBody> bodyList = updatedEHSObject.getBody();
        System.out.println("Body List: \n" + bodyList);
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("Random EHSObject Data: \n" + updatedEHSObject);

        System.out.println("--------------------------------------------------");
        System.out.println();
        // ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();

        mapper.writeValue(writer, bodyList);
        // String jsonString = mapper.writeValueAsString(bodyList);
        // System.out.println("jsonString >> "+jsonString);
        return postData(writer.toString());

        //return postData(mapper.writeValueAsString(ehsObj));

    }*/
    
    /*private EHSObjectVO getEHSObject(EHSObjectVO message, Long currentTimeMillis){
        EHSObjectVO messageObject = new EHSObjectVO();
        ArrayList<Long> datapoint = new ArrayList<>();
        datapoint.add(currentTimeMillis);
        datapoint.add(10l);
        datapoint.add(1l);
        messageObject.setMessageId(currentTimeMillis);
        for (AssetBody ehsArea : message.getBody()) {
            AssetBody body = new AssetBody();
            if(ehsArea.getO3()!= null){
                body.setO3(ehsArea.getO3());
            }
            if(ehsArea.getNH3()!= null){
                body.setNH3(ehsArea.getNH3());
            }
            if(ehsArea.getNO2()!= null){
                body.setNO2(ehsArea.getNO2());
            }
            if(ehsArea.getPB()!= null){
                body.setPB(ehsArea.getPB());
            }
            if(ehsArea.getCO2()!= null){
                body.setCO2(ehsArea.getCO2());
            }
            if(ehsArea.getSO2()!= null){
                body.setSO2(ehsArea.getSO2());
            }
            if(ehsArea.getPM2_5()!= null){
                body.setPM2_5(ehsArea.getPM2_5());
            }
            if(ehsArea.getPM10()!= null){
                body.setPM10(ehsArea.getPM10());
            }
            
            body.setName(ehsArea.getName());
            body.getDatapoints().add(datapoint);

            messageObject.getBody().add(body);

        }
        return messageObject;
    }*/

    /*
     * private ArrayList<HashMap<String, Object>>
     * pickValueFromGeneratedJson(MessageObject messageObject) {
     * ArrayList<HashMap<String, Object>> values = new ArrayList<>(); for (Body
     * body : messageObject.getBody()) { HashMap<String, Object> innerValue =
     * new HashMap<>(); innerValue.put("name", body.getName());
     * innerValue.put("o3", pickOneValue(body.getO3())); innerValue.put("nh3",
     * pickOneValue(body.getNH3())); innerValue.put("no2",
     * pickOneValue(body.getNO2())); innerValue.put("pb",
     * pickOneValue(body.getPB())); innerValue.put("co2",
     * pickOneValue(body.getCO2())); innerValue.put("so2",
     * pickOneValue(body.getSO2())); innerValue.put("pm2_5",
     * pickOneValue(body.getPM2_5())); innerValue.put("pm10",
     * pickOneValue(body.getPM10())); values.add(innerValue); } return values; }
     */
    // select one of the value from the list with random index
    private Double pickOneValue(ArrayList<Double> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }

    /**
     * @return -
     */

    List<JSONData> generateMockDataMap_RT() {
        String machineControllerId = this.applicationProperties.getMachineControllerId();
        List<JSONData> list = new ArrayList<JSONData>();
        JSONData data = new JSONData();
        data.setName("Compressor-2015:CompressionRatio"); //$NON-NLS-1$
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(2.5, 3.0) - 1) * 65535.0 / 9.0);
        data.setDatatype("DOUBLE"); //$NON-NLS-1$
        data.setRegister(""); //$NON-NLS-1$
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:DischargePressure"); //$NON-NLS-1$
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(0.0, 23.0) * 65535.0) / 100.0);
        data.setDatatype("DOUBLE"); //$NON-NLS-1$
        data.setRegister(""); //$NON-NLS-1$
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:SuctionPressure"); //$NON-NLS-1$
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(0.0, 0.21) * 65535.0) / 100.0);
        data.setDatatype("DOUBLE"); //$NON-NLS-1$
        data.setRegister(""); //$NON-NLS-1$
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:MaximumPressure"); //$NON-NLS-1$
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(22.0, 26.0) * 65535.0) / 100.0);
        data.setDatatype("DOUBLE"); //$NON-NLS-1$
        data.setRegister(""); //$NON-NLS-1$
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:MinimumPressure"); //$NON-NLS-1$
        data.setTimestamp(getCurrentTimestamp());
        data.setValue(0.0);
        data.setDatatype("DOUBLE"); //$NON-NLS-1$
        data.setRegister(""); //$NON-NLS-1$
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:Temperature"); //$NON-NLS-1$
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(65.0, 80.0) * 65535.0) / 200.0);
        data.setDatatype("DOUBLE"); //$NON-NLS-1$
        data.setRegister(""); //$NON-NLS-1$
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:Velocity"); //$NON-NLS-1$
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(0.0, 0.1) * 65535.0) / 0.5);
        data.setDatatype("DOUBLE"); //$NON-NLS-1$
        data.setRegister(""); //$NON-NLS-1$
        data.setUnit(machineControllerId);
        list.add(data);

        return list;
    }

    private Timestamp getCurrentTimestamp() {
        java.util.Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        return ts;
    }

    private static double generateRandomUsageValue(double low, double high) {
        return low + Math.random() * (high - low);
    }
    
    private String postData(String content) {
        HttpClient client = null;
        try {
        	
            HttpClientBuilder builder = HttpClientBuilder.create();
            if (this.applicationProperties.getDiServiceProxyHost() != null && !"".equals(this.applicationProperties.getDiServiceProxyHost()) //$NON-NLS-1$
                    && this.applicationProperties.getDiServiceProxyPort() != null && !"".equals(this.applicationProperties.getDiServiceProxyPort())) //$NON-NLS-1$
            {
            	HttpHost proxy = new HttpHost(this.applicationProperties.getDiServiceProxyHost(), Integer.parseInt(this.applicationProperties.getDiServiceProxyPort()));
                builder.setProxy(proxy);
            }
            
            //HttpHost proxy = new HttpHost("pitc-zscaler-aspac-bangalore3pr.proxy.corporate.ge.com",443);
            //builder.setProxy(proxy);
            
            client = builder.build();
            String serviceURL = null;
            if (this.applicationProperties.getPredixDataIngestionURL() == null) {
                //serviceURL = "http://" + this.applicationProperties.getDiServiceHost() + ":" + this.applicationProperties.getDiServicePort() + "/saveTimeSeriesData"; //$NON-NLS-1$
                serviceURL = "https://" + this.applicationProperties.getDiServiceHost() + "/saveTimeSeriesData"; //$NON-NLS-1$
                URLEncoder.encode(serviceURL, "UTF-8");
            } else {
                serviceURL = this.applicationProperties.getPredixDataIngestionURL() + "/saveTimeSeriesData"; //$NON-NLS-1$
                URLEncoder.encode(serviceURL, "UTF-8");

            }
            log.info("Service URL : " + serviceURL); //$NON-NLS-1$
            log.info("Data : " + content);
            HttpPost request = new HttpPost(serviceURL);
            HttpEntity reqEntity = MultipartEntityBuilder.create().addTextBody("content", content) //$NON-NLS-1$
            		.addTextBody("clientId", "client1")
            		//.addTextBody("destinationId", "TimeSeries").addTextBody("clientId", "TimeSeries") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    .addTextBody("tenantId", this.applicationProperties.getTenantId()).build(); //$NON-NLS-1$
            request.setEntity(reqEntity);
            
            HttpResponse response = client.execute(request);
            log.debug("Send Data to Ingestion Service : Response Code : " + response.getStatusLine().getStatusCode()); //$NON-NLS-1$
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = ""; //$NON-NLS-1$
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.debug("Response : " + result.toString());
            if (result.toString().startsWith("You successfully posted")) { //$NON-NLS-1$
                return "SUCCESS : " + result.toString(); //$NON-NLS-1$
            }
            return "FAILED : " + result.toString(); //$NON-NLS-1$

        } catch (Throwable e) {
            log.error("unable to post data ", e); //$NON-NLS-1$
            return "FAILED : " + e.getLocalizedMessage(); //$NON-NLS-1$
        }
    }

    /*
     * private String postData(String content) { List<NameValuePair> parameters
     * = new ArrayList<NameValuePair>(); parameters.add(new
     * BasicNameValuePair("content", content)); parameters.add(new
     * BasicNameValuePair("destinationId", "TimeSeries")); parameters.add(new
     * BasicNameValuePair("clientId", "TimeSeries")); parameters.add(new
     * BasicNameValuePair("tenantId",
     * this.applicationProperties.getTenantId()));
     * 
     * 
     * EntityBuilder builder = EntityBuilder.create();
     * builder.setParameters(parameters); HttpEntity reqEntity =
     * builder.build();
     * 
     * String serviceURL =
     * this.applicationProperties.getPredixDataIngestionURL(); //$NON-NLS-1$ if
     * (serviceURL == null) { serviceURL =
     * this.applicationProperties.getDiServiceURL(); } if (serviceURL != null) {
     * try(CloseableHttpResponse response = restClient.post(serviceURL,
     * reqEntity, null, 100, 1000);) {
     * 
     * log.info("Service URL : " + serviceURL); //$NON-NLS-1$ log.info("Data : "
     * +content);
     * 
     * log.debug("Send Data to Ingestion Service : Response Code : " +
     * response.getStatusLine().getStatusCode()); //$NON-NLS-1$ BufferedReader
     * rd = new BufferedReader(new
     * InputStreamReader(response.getEntity().getContent())); StringBuffer
     * result = new StringBuffer(); String line = ""; //$NON-NLS-1$ while ((line
     * = rd.readLine()) != null) { result.append(line); } log.info("Response : "
     * +result.toString()); if (result.toString().startsWith(
     * "You successfully posted")) { //$NON-NLS-1$ return "SUCCESS : "
     * +result.toString(); //$NON-NLS-1$ } return "FAILED : "+result.toString();
     * //$NON-NLS-1$
     * 
     * } catch (IOException e) { log.error("unable to post data ", e);
     * //$NON-NLS-1$ return "FAILED : "+e.getLocalizedMessage(); //$NON-NLS-1$ }
     * }else{ return "Dataingestion Service URL is empty."; } }
     */
}




