package com.tony.mapinspector.entity;

import java.util.Date;

public class LightMapping {
	private Long id;
	private Date lastUpdated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private String fromClass;

	private String toClass;

	private String pkg4Code;

	private String pkg4UT;

	public String getPkg4Code() {
		return pkg4Code;
	}

	public void setPkg4Code(String pkg4Code) {
		this.pkg4Code = pkg4Code;
	}

	public String getPkg4UT() {
		return pkg4UT;
	}

	public void setPkg4UT(String pkg4ut) {
		pkg4UT = pkg4ut;
	}

	public String getFromClass() {
		return fromClass;
	}

	public void setFromClass(String fromClass) {
		this.fromClass = fromClass;
	}

	public String getToClass() {
		return toClass;
	}

	public void setToClass(String toClass) {
		this.toClass = toClass;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}
