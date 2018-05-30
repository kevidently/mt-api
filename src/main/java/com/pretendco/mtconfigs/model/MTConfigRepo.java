package com.pretendco.mtconfigs.model;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MTConfigRepo extends CrudRepository<MTConfig, Long>{
	@Query(value="select * from mtconfig", nativeQuery = true)
	List<MTConfig> getConfigs();
	
	@Query(value="select * from mtconfig where connectorid='kantan'", nativeQuery = true)
	List<MTConfig> getKantanConfigs();
	
	@Query(value="select value from keys where id=?1", nativeQuery = true)
	String getCompanyMTKey(int keyId);
}