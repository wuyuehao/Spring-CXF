package com.tony.mapinspector.entity;

import java.io.Serializable;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

@Entity
public class Pair implements Serializable{
	
	private static final long serialVersionUID = -6279265576765240129L;
	
	@Transient
	private Logger log = Logger.getLogger(Pair.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private String name;
	
	private String type;
	
	private String toName;
	
	private String toType;
	
	private String varName;
	
	private String toVarName;
	
	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getToVarName() {
		return toVarName;
	}

	public void setToVarName(String toVarName) {
		this.toVarName = toVarName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getToType() {
		return toType;
	}

	public void setToType(String toType) {
		this.toType = toType;
	}

	public String getGetter() {
		return "get" + name.substring(name.lastIndexOf('.')+1);
    }
	
	public String getSetter() {
		return "set" + toName.substring(toName.lastIndexOf('.')+1);
	}

	public String getFromVar() {
	    return getVarName(name.substring(name.lastIndexOf('.')+1));
    }
	
	public String getToParentVar() {
		if(!toName.contains(".")){
			return "";
		}
		String parent = toName.substring(0, toName.lastIndexOf('.'));
		parent = parent.substring(parent.lastIndexOf('.')+1);
		 
		return getVarName(parent + "VO");
	}
	
	private String getVarName(String simpleName) {
		return Character.toLowerCase(simpleName.charAt(0))
		        + simpleName.substring(1);
	}

}
