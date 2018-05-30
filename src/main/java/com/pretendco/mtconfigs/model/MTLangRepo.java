package com.pretendco.mtconfigs.model;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MTLangRepo extends CrudRepository<MTLang, Long>{
	@Query(value="select * from mtlang", nativeQuery = true)
	List<MTLang> getLangs();
	
	@Query(value="select pair from mtlang", nativeQuery = true)
	List<String> getPairs();
	
	@Query(value="select value from keys where id=?1", nativeQuery = true)
	String getCompanyMTKey(int keyId);
}