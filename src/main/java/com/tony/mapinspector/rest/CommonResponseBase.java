package com.tony.mapinspector.rest;

public class CommonResponseBase {
	
	private String message;
	
	private Long idCreated;

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
