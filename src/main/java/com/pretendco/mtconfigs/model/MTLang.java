package com.pretendco.mtconfigs.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mtlang")
public class MTLang implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long Id;

	@Column(name = "pair")
	private String Pair;
	
	@Column(name = "source")
	private String Source;

	@Column(name = "target")
	private String Target;

	protected MTLang() {
	}

	public MTLang(Long id, String pair, String source, String target) 
	{
		Id = id;
		Pair = pair;
		Source = source;
		Target = target;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getPair() {
		return Pair;
	}

	public void setPair(String pair) {
		Pair = pair;
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

}
