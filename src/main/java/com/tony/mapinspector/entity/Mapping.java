package com.tony.mapinspector.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Mapping implements Serializable {

	private static final long serialVersionUID = -5540811920907021749L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	private String fromClass;

	private String toClass;

	private Date lastUpdated;
	
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

	private HashMap<String, String> varNames = new HashMap<String, String>();

	public HashMap<String, String> getVarNames() {
		return varNames;
	}

	public void setVarNames(HashMap<String, String> varNames) {
		this.varNames = varNames;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Pair> pairs;

	public List<Pair> getPairs() {
		return pairs;
	}

	public void setPairs(List<Pair> pairs) {
		this.pairs = pairs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean containsToId(String id) {
		for (Pair p : pairs) {
			if (p.getToName().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsChildrenOfToId(String id) {
		for (Pair p : pairs) {
			if (!p.getToName().equals(id) && p.getToName().startsWith(id)) {
				return true;
			}
		}
		return false;
	}

	public Pair getFirstChildrenOfToId(String id) {
		for (Pair p : pairs) {
			if (!p.getToName().equals(id) && p.getToName().startsWith(id)) {
				return p;
			}
		}
		return null;
	}

	public boolean containsFromId(String id) {
		for (Pair p : pairs) {
			if (p.getName().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsChildrenOfFromId(String id) {
		for (Pair p : pairs) {
			if (!p.getName().equals(id) && p.getName().startsWith(id)) {
				return true;
			}
		}
		return false;
	}

	public Pair getPairByToName(String name) {
		for (Pair p : pairs) {
			if (p.getToName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public String getFromClassVarName() {
		return getVarName(fromClass.substring(fromClass.lastIndexOf('.') + 1));
	}

	public String getToClassVarName() {
		return getVarName(toClass.substring(toClass.lastIndexOf('.') + 1));
	}

	private String getVarName(String simpleName) {
		return Character.toLowerCase(simpleName.charAt(0))
		        + simpleName.substring(1);
	}

	public Pair getFirstDirectChildrenOfToId(String id) {
		for (Pair p : pairs) {
			if (!p.getToName().equals(id) && p.getToName().startsWith(id)) {
				if (!p.getToName().substring(id.length() + 1).contains(".")) {
					return p;
				}
			}
		}
		return null;
	}

}
