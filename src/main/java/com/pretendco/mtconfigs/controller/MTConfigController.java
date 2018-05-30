package com.pretendco.mtconfigs.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pretendco.mtconfigs.model.MTConfig;
import com.pretendco.mtconfigs.model.MTConfigRepo;

@RestController
public class MTConfigController {

	private String MTKEY;

	@Autowired
	MTConfigRepo mtcRepo;
	
	@PostConstruct
	private void getKey () { MTKEY = mtcRepo.getCompanyMTKey(1); }
	
	// Create config
	@RequestMapping(
		value="/config", 
		method=RequestMethod.POST, 
		consumes=MediaType.APPLICATION_JSON_VALUE, 
		produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createConfig (@RequestHeader(value="MT-Key", required=true) String headerKey,
		@RequestBody(required=true) MTConfig mtc)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }

		MTConfig newMTC = null;
		if ( mtc != null && mtc.getId() == null ) {
			newMTC = mtcRepo.save(mtc);
		}
		else {
			throw new RuntimeException("Invalid parameters for creating new MT Config. The value for 'id' should be null.");
		}
		return new ResponseEntity<MTConfig>(newMTC, HttpStatus.CREATED);
	}

	// Read config
	@RequestMapping(value="/config/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getConfig (@RequestHeader(value="MT-Key", required=true) String headerKey, 
		@PathVariable("id") Long id)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }

		MTConfig mtc = mtcRepo.findOne(id);
		return new ResponseEntity<MTConfig>(mtc, HttpStatus.OK);
	}
	
	// Update config
	@RequestMapping(
		value="/config", 
		method=RequestMethod.PUT, 
		consumes=MediaType.APPLICATION_JSON_VALUE, 
		produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateConfig (@RequestHeader(value="MT-Key", required=true) String headerKey,
		@RequestBody(required=true) MTConfig mtc) 
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		if ( mtc != null && mtc.getId() != null ) {
			mtcRepo.save(mtc);
		}
		return new ResponseEntity<MTConfig>(mtc, HttpStatus.OK);
	}
	
	// Delete config
	@RequestMapping(value="/config/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteConfig (@RequestHeader(value="MT-Key", required=true) String headerKey,
		@PathVariable("id") Long id)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		MTConfig mtc = mtcRepo.findOne(id);
		if ( mtc != null) { mtcRepo.delete(mtc); }
		return new ResponseEntity<MTConfig>(mtc, HttpStatus.NO_CONTENT);
	}

	// Get all MT configs
	@RequestMapping(value="/configs", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getConfigs (@RequestHeader(value="MT-Key", required=true) String headerKey)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		List<MTConfig> mtConfigs = new ArrayList<>();
		mtConfigs = mtcRepo.getConfigs();
		return new ResponseEntity<List<MTConfig>>(mtConfigs, HttpStatus.OK);
	}
	
	
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="Invalid value for 'MT-Key'")
	public class InvalidKeyException extends RuntimeException {
	
	}

}