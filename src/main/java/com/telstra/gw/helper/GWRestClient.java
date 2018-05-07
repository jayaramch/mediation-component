package com.telstra.gw.helper;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.telstra.gw.models.SoapXmlEnvelope;
import com.telstra.gw.parser1.SendToSQGateway;

@Component
public class GWRestClient {
	private static final Logger logger = Logger.getLogger(GWRestClient.class);
	
	@Autowired			
	public SendToSQGateway sendToSQGateway;
	
	@Autowired
	public RestTemplate restTemplate;
	
	public void deligateRequestToGW(SoapXmlEnvelope envelope) {
		try {
			String url = "";
			JSONObject jsonObject = new JSONObject();
			jsonObject.clear();
			jsonObject = sendToSQGateway.sendToGateway(envelope);
			url = sendToSQGateway.getURL(envelope);
			logger.info("URL " + url);
			
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(jsonObject,headers);
	        logger.info("Request after converting into json " + entity);
	        
	        String response = restTemplate.postForObject(url, entity, String.class);
			logger.info("Response from Gateway is " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
