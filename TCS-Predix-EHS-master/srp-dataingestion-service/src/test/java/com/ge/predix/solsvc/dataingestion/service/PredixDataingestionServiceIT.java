package com.ge.predix.solsvc.dataingestion.service;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ge.predix.solsvc.dataingestion.boot.DataingestionServiceApplication;
import com.ge.predix.solsvc.dataingestion.service.type.JSONData;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix -
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataingestionServiceApplication.class)
@WebAppConfiguration
@IntegrationTest(
{
    "server.port=9191"
})
public class PredixDataingestionServiceIT
{
    @Value("${local.server.port}")
    private int          localServerPort;

    private URL          base;

    private RestTemplate template;
    
    @Autowired
	protected RestClient restClient;

    /**
     * @throws Exception -
     */
    @Before
    public void setUp()
            throws Exception
    {
        this.template = new TestRestTemplate();
        this.localServerPort = 9191;
    }

    /**
     * @throws MalformedURLException -
     * @throws Exception -
     */
    @SuppressWarnings("nls")
    @Test
    public void ping() throws MalformedURLException
    {
        this.base = new URL("http://localhost:" + this.localServerPort + "/ping");
        ResponseEntity<String> response = this.template.getForEntity(this.base.toString(), String.class);
        assertThat(response.getBody(), startsWith("SUCCESS"));
    }
    
    //@Test
    //public void postTimeSeries

    /**
     * @throws IOException -
     * @throws JsonMappingException -
     * @throws JsonGenerationException -
     * @throws Exception -
     */
    @SuppressWarnings("nls")
    @Test
    public void dataIngestionTest() throws JsonGenerationException, JsonMappingException, IOException
    {
        this.base = new URL("http://localhost:" + this.localServerPort + "/saveTimeSeriesData");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        List<JSONData> list = generateMockDataMap_RT();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();

        mapper.writeValue(writer, list);
        map.add("content", writer.toString());
        map.add("clientId", "client1");
        map.add("tenantId", null);
        
        String authorization = "bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiI5NTc0MzNkY2YzZjA0OGE1YmE2ODlhMmY4MGExZGRlOSIsInN1YiI6ImNsaWVudDEiLCJzY29wZSI6WyJ0aW1lc2VyaWVzLnpvbmVzLmJkN2FiZDQyLTk4ZWYtNGRmMi05MTQxLTNlMGRhN2JhNzdjMC5pbmdlc3QiLCJ0aW1lc2VyaWVzLnpvbmVzLmJkN2FiZDQyLTk4ZWYtNGRmMi05MTQxLTNlMGRhN2JhNzdjMC51c2VyIiwidWFhLnJlc291cmNlIiwib3BlbmlkIiwidWFhLm5vbmUiLCJ0aW1lc2VyaWVzLnpvbmVzLmJkN2FiZDQyLTk4ZWYtNGRmMi05MTQxLTNlMGRhN2JhNzdjMC5xdWVyeSIsInByZWRpeC1hc3NldC56b25lcy4zMzk2NGMzMy05YzVlLTRjYWMtOTYyYi02ODQ3ZTM1YTE2MDAudXNlciJdLCJjbGllbnRfaWQiOiJjbGllbnQxIiwiY2lkIjoiY2xpZW50MSIsImF6cCI6ImNsaWVudDEiLCJncmFudF90eXBlIjoiY2xpZW50X2NyZWRlbnRpYWxzIiwicmV2X3NpZyI6IjRlY2Y2ZDIwIiwiaWF0IjoxNDc1MDY4NDc0LCJleHAiOjE0NzUxMTE2NzQsImlzcyI6Imh0dHBzOi8vM2Y2MDcxZjYtZDBjMy00NTQxLWI4M2UtMmE3ZjI5ZjAyNGU3LnByZWRpeC11YWEucnVuLmF3cy11c3cwMi1wci5pY2UucHJlZGl4LmlvL29hdXRoL3Rva2VuIiwiemlkIjoiM2Y2MDcxZjYtZDBjMy00NTQxLWI4M2UtMmE3ZjI5ZjAyNGU3IiwiYXVkIjpbInVhYSIsIm9wZW5pZCIsInRpbWVzZXJpZXMuem9uZXMuYmQ3YWJkNDItOThlZi00ZGYyLTkxNDEtM2UwZGE3YmE3N2MwIiwicHJlZGl4LWFzc2V0LnpvbmVzLjMzOTY0YzMzLTljNWUtNGNhYy05NjJiLTY4NDdlMzVhMTYwMCIsImNsaWVudDEiXX0.LDUZieQN9xyFdgWJ7F9O3KAEroB0jeSpSEYwf1kqil7SeZXWGR3rcilbmChekhU4R6je8wrhIplSVqQSjWQkgFpW0RB9wEC8FEcg7KVpzE-BcA-zcBHE3_m6TiZD1cnwhSRZgYVqh2yQ-OE0FUeU2xrCEwvCD-D6TXCdZpG_J_28pm83ae6__n7du6m3kco_l6UpzyXRfoYA9rIvmzsCzEuUepOmlAq5Van8OGq1cQEgUOkSjD_K2_-c_RtFXPuGLHywFM-v9kQXhC74rY0x1MkRPfn_ojYhPlnB9-TSTWMDOEjsjIBFswe4xnanYIg4115iqnHfQKMWoudtVz0YUQ";
        map.add("Authorization", authorization);
        
       /* List<Header> headers = null;
		if (StringUtils.isEmpty(authorization)) {
			System.out.println("getting Headers");
			headers = this.restClient.getSecureTokenForClientId();
			System.out.println("headers : " + headers);
		} else {
			headers = new ArrayList<Header>();
			this.restClient.addSecureTokenToHeaders(headers, authorization);
		}
        
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		
		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("pitc-zscaler-aspac-bangalore3pr.proxy.corporate.ge.com", 443));
		requestFactory.setProxy(proxy);
		
		
		
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers1.add(headers.get(0).getName(), headers.get(0).getValue());
		headers1.add("predix-zone-id", "33964c33-9c5e-4cac-962b-6847e35a1600");
		HttpEntity<String> entity = new HttpEntity<String>(headers1);*/
		
        String response = this.template.postForObject(this.base.toString(), map, String.class);
        assertThat(response, startsWith("You successfully posted data"));

    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    List<JSONData> generateMockDataMap_RT()
    {
        String machineControllerId = "678";
        List<JSONData> list = new ArrayList<JSONData>();
        JSONData data = new JSONData();
        data.setName("Compressor-2015:CompressionRatio");
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(2.5, 3.0) - 1) * 65535.0 / 9.0);
        data.setDatatype("DOUBLE");
        data.setRegister("");
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:DischargePressure");
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(0.0, 23.0) * 65535.0) / 100.0);
        data.setDatatype("DOUBLE");
        data.setRegister("");
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:SuctionPressure");
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(0.0, 0.21) * 65535.0) / 100.0);
        data.setDatatype("DOUBLE");
        data.setRegister("");
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:MaximumPressure");
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(22.0, 26.0) * 65535.0) / 100.0);
        data.setDatatype("DOUBLE");
        data.setRegister("");
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:MinimumPressure");
        data.setTimestamp(getCurrentTimestamp());
        data.setValue(0.0);
        data.setDatatype("DOUBLE");
        data.setRegister("");
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:Temperature");
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(65.0, 80.0) * 65535.0) / 200.0);
        data.setDatatype("DOUBLE");
        data.setRegister("");
        data.setUnit(machineControllerId);
        list.add(data);

        data = new JSONData();
        data.setName("Compressor-2015:Velocity");
        data.setTimestamp(getCurrentTimestamp());
        data.setValue((generateRandomUsageValue(0.0, 0.1) * 65535.0) / 0.5);
        data.setDatatype("DOUBLE");
        data.setRegister("");
        data.setUnit(machineControllerId);
        list.add(data);

        return list;
    }

    private Timestamp getCurrentTimestamp()
    {
        java.util.Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        return ts;
    }

    private static double generateRandomUsageValue(double low, double high)
    {
        return low + Math.random() * (high - low);
    }

}
