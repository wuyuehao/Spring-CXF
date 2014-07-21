package com.tony.mapinspector.rest;

import java.util.Set;

public class CommonResponseBase {
	
	public Set<String> getSet() {
		return set;
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}

	private String message;
	
	private Long idCreated;
	
	private Set<String> set;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getIdCreated() {
		return idCreated;
	}

	public void setIdCreated(Long idCreated) {
		this.idCreated = idCreated;
	}
	
	
}
