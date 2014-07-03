package com.tony.mapinspector.rest;

public class FooBar {
	private String val;
	private String key;

	public FooBar(String key, String val) {
		this.val = val;
		this.key = key;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
