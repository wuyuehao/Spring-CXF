package com.tony.mapinspector.entity;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Mapping implements Serializable {

	private static final long serialVersionUID = -5540811920907021749L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	private String uid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	private ArrayList<Pair> pairs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArrayList<Pair> getPairs() {
		return pairs;
	}

	public void setPairs(ArrayList<Pair> pairs) {
		this.pairs = pairs;
	}

}
