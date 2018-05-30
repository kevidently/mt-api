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

import com.pretendco.mtconfigs.model.MTLang;
import com.pretendco.mtconfigs.model.MTLangRepo;

@RestController
public class MTLangController {

	private String MTKEY;

	@Autowired
	MTLangRepo mtlRepo;

	@PostConstruct
	private void getKey () { MTKEY = mtlRepo.getCompanyMTKey(1); }

	// Create lang
	@RequestMapping(
		value="/lang", 
		method=RequestMethod.POST, 
		consumes=MediaType.APPLICATION_JSON_VALUE, 
		produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createLang (@RequestHeader(value="MT-Key", required=true) String headerKey,
		@RequestBody(required=true) MTLang mtl)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }

		MTLang newMTL = null;
		if ( mtl != null && mtl.getId() == null ) {
			newMTL = mtlRepo.save(mtl);
		}
		else {
			throw new RuntimeException("Invalid parameters for creating new MT Lang. The value for 'id' should be null.");
		}
		return new ResponseEntity<MTLang>(newMTL, HttpStatus.CREATED);
	}

	// Read lang
	@RequestMapping(value="/lang/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLang (@RequestHeader(value="MT-Key", required=true) String headerKey, 
		@PathVariable("id") Long id)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }

		MTLang mtl = mtlRepo.findOne(id);
		return new ResponseEntity<MTLang>(mtl, HttpStatus.OK);
	}
	
	// Update lang
	@RequestMapping(
		value="/lang", 
		method=RequestMethod.PUT, 
		consumes=MediaType.APPLICATION_JSON_VALUE, 
		produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateLang (@RequestHeader(value="MT-Key", required=true) String headerKey,
		@RequestBody(required=true) MTLang mtl) 
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		if ( mtl != null && mtl.getId() != null ) {
			mtlRepo.save(mtl);
		}
		return new ResponseEntity<MTLang>(mtl, HttpStatus.OK);
	}
	
	// Delete lang
	@RequestMapping(value="/lang/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteLang (@RequestHeader(value="MT-Key", required=true) String headerKey,
		@PathVariable("id") Long id)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		MTLang mtl = mtlRepo.findOne(id);
		if ( mtl != null) { mtlRepo.delete(mtl); }
		return new ResponseEntity<MTLang>(mtl, HttpStatus.NO_CONTENT);
	}

	
	// Get all MT languages
	@RequestMapping(value="/langs", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLangs (@RequestHeader(value="MT-Key", required=true) String headerKey)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		List<MTLang> mtLangs = new ArrayList<>();
		mtLangs = mtlRepo.getLangs();
		return new ResponseEntity<List<MTLang>>(mtLangs, HttpStatus.OK);
	}
	
	// Get all MT pairs
	@RequestMapping(value="/pairs", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getPairs (@RequestHeader(value="MT-Key", required=true) String headerKey)
	{
		// Check for Company MT Key
		if ( MTKEY != null && !headerKey.equals(MTKEY) ) { throw new InvalidKeyException(); }
		
		List<String> mtPairs = new ArrayList<>();
		mtPairs = mtlRepo.getPairs();
		return new ResponseEntity<List<String>>(mtPairs, HttpStatus.OK);
	}
	
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="Invalid value for 'MT-Key'")
	public class InvalidKeyException extends RuntimeException {
	
	}

}