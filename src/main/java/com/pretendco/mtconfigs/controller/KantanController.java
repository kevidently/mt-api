package com.pretendco.mtconfigs.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pretendco.mtconfigs.model.MTConfig;
import com.pretendco.mtconfigs.model.MTConfigRepo;

@RestController
public class KantanController {

	private static final String API_SERVER_URL = "https://app.kantanmt.com/api/";
	private static final String KANTANAPITOKEN = "";
	private String MTKEY;

	private JSONParser parser;
	private CloseableHttpClient httpClient = null;
	
	@Autowired
	MTConfigRepo mtcRepo;

	@PostConstruct
	private void getKey () { MTKEY = mtcRepo.getCompanyMTKey(1); }
	
	// Endpoint for retrieving Kantan Engines
	@RequestMapping(value="/kantan-engines", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEngines (@RequestHeader(value="MT-Key", required=true) String headerKey)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		JSONObject responseData = fetchKantanEngines();
		return new ResponseEntity<JSONObject>(responseData, HttpStatus.OK);
	}

	// Endpoint for synchronizing Kantan MT Configs with Kantan Engines
	@RequestMapping(value="/synchronized-configs", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> syncKantan (@RequestHeader(value="MT-Key", required=true) String headerKey)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		JSONObject responseData = fetchKantanEngines();
		JSONArray apiEngines = (JSONArray)responseData.get("engines");

		List <MTConfig> currentKantanConfigs = mtcRepo.getKantanConfigs();
		
		List <Object> matchedEngines = new ArrayList<>();
		List <Object> newEngines = new ArrayList<>();
		
		if ( currentKantanConfigs.isEmpty() ) {
			// No existing kantan mt configs, so use all engines to create all new configs
			for ( Object apiEngine : apiEngines ) {
				newEngines.add(apiEngine);
			}
		}
		else {
			// Determine the course of action for each existing config compared to the engines
			for ( MTConfig mtc : currentKantanConfigs ) {
				if ( mtc.getConnectorId().equals("kantan") ) {

					try {
						String existingConfigParams = mtc.getParams();
						JSONObject parsedExistingConfigParams = (JSONObject)parser.parse(existingConfigParams);
						String existingEngineName = (String)parsedExistingConfigParams.get("engine");

						boolean configHasMatchingEngine = false;
						
						for ( Object apiEngine : apiEngines ) {
							// First check if we have a config for this engine, if so, then update it in DB
								JSONObject apiEngineJson = (JSONObject) apiEngine;
								String apiEngineName = (String)apiEngineJson.get("name");
								
								if ( apiEngineName.equals(existingEngineName) ) {
									configHasMatchingEngine = true;
									String apiEngineSource = (String)apiEngineJson.get("src");
									String apiEngineTarget = (String)apiEngineJson.get("trg");
									
									// Do the update
									mtc.setSource(apiEngineSource+";");
									mtc.setTarget(apiEngineTarget+";");
									mtcRepo.save(mtc);
									matchedEngines.add(apiEngine);
									break;
								}
						}
						
						// Delete config if needed
						if ( configHasMatchingEngine == false ) {
							// We compared all engines to this mt config, and found no match, so delete it
							mtcRepo.delete(mtc);
						}
						
					}
					catch (ParseException e) {
						throw new RuntimeException("Error getting engine info for config id "+mtc.getId().toString()+"."+e.getMessage(), e);
					}
				}
			}
		}

		// Determine the api engines that did not have a matching mt config
		for ( Object apiEngine : apiEngines ) {
			boolean hasMatchingEngine = false;
			for ( Object matchedEngine : matchedEngines ) {
				if ( apiEngine == matchedEngine ) { 
					hasMatchingEngine = true;
					break;
				}
			}
			if ( !hasMatchingEngine ) {
				newEngines.add(apiEngine);
			}
		}
		
		// If needed, add a new mt config for each new/unmatched engine
		if ( !newEngines.isEmpty() ) {
			Long configId = null;
			int clientId = 751;
			String connectorId = "kantan";
			String variant = null;
			int sequence = 40;
			String site = null;
			for ( Object newEngine : newEngines ) {
				JSONObject newEngineJson = (JSONObject) newEngine;
				String source = (String)newEngineJson.get("src")+";";
				String target = (String)newEngineJson.get("trg")+";";
				String params = "{ \"apiToken\": \""+KANTANAPITOKEN+"\", \"engine\": \""+newEngineJson.get("name")+"\" }";
				String description = "KantanMT "+newEngineJson.get("name");
				MTConfig newMtc = new MTConfig(configId, clientId, source, target, connectorId, variant, description, params, sequence, site);
				mtcRepo.save(newMtc);
			}
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
	

	private JSONObject fetchKantanEngines () 
	{
		JSONObject tmpData = null;
		httpClient = HttpClientBuilder.create().build();
		parser = new JSONParser();
		HttpGet httpget = new HttpGet(API_SERVER_URL+"query/"+KANTANAPITOKEN+"/engines");
		try ( CloseableHttpResponse resp = httpClient.execute(httpget) ) {
			tmpData = readContent(resp);
		}
		catch ( Exception e ) {
			throw new RuntimeException("Error getting the engines: "+e.getMessage(), e);
		}
		
		return tmpData;
	}

	private JSONObject readContent (HttpResponse response) throws IOException, ParseException
	{
		String body = null;
		HttpEntity entity = response.getEntity();
		if ( entity == null ) return null;
		try ( InputStream stream = entity.getContent() ) {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			body = br.readLine();
		}
		EntityUtils.consume(entity);
		JSONObject parsedData = (JSONObject)parser.parse(body);
		JSONObject responseData = (JSONObject)parsedData.get("response");
		return (JSONObject)responseData.get("body");
	}

	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="Invalid value for 'MT-Key'")
	public class InvalidKeyException extends RuntimeException {
	
	}

}