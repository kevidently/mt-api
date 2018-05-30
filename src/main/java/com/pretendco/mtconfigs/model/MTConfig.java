package com.pretendco.mtconfigs.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mtconfig")
public class MTConfig implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long Id;

	@Column(name = "clientid")
	private int ClientId;
	
	@Column(name = "source")
	private String Source;

	@Column(name = "target")
	private String Target;
	
	@Column(name = "connectorid")
	private String ConnectorId;

	@Column(name = "variant")
	private String Variant;
	
	@Column(name = "description")
	private String Description;

	@Column(name = "params")
	private String Params;
	
	@Column(name = "sequence")
	private int Sequence;
	
	@Column(name = "site")
	private String Site;

	protected MTConfig() {
	}

	public MTConfig(Long id, int clientId, String source, String target, String connectorId, String variant, String description,
			String params, int sequence, String site) 
	{
		Id = id;
		ClientId = clientId;
		Source = source; 
		Target = target; 
		ConnectorId = connectorId; 
		Variant = variant;
		Description = description;
		Params = params;
		Sequence = sequence;
		Site = site;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public int getClientId() {
		return ClientId;
	}

	public void setClientId(int clientId) {
		ClientId = clientId;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getTarget() {
		return Target;
	}

	public void setTarget(String target) {
		Target = target;
	}

	public String getConnectorId() {
		return ConnectorId;
	}

	public void setConnectorId(String connectorId) {
		ConnectorId = connectorId;
	}

	public String getVariant() {
		return Variant;
	}

	public void setVariant(String variant) {
		Variant = variant;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getParams() {
		return Params;
	}

	public void setParams(String params) {
		Params = params;
	}
	
	public int getSequence() {
		return Sequence;
	}

	public void setSequence(int sequence) {
		Sequence = sequence;
	}

	public String getSite() {
		return Site;
	}

	public void setSite(String site) {
		Site = site;
	}

}
