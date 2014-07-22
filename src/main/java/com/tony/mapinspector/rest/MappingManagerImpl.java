package com.tony.mapinspector.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tony.mapinspector.dao.MappingDao;
import com.tony.mapinspector.entity.LightMapping;
import com.tony.mapinspector.entity.Mapping;
import com.tony.mapinspector.entity.Pair;

public class MappingManagerImpl implements IMappingManager {

	private Logger log = Logger.getLogger(MappingManagerImpl.class);

	@Autowired
	private MappingDao mappingDao;

	public List<LightMapping> readByClassName(String className,
	        HttpServletResponse response) {
		List<Mapping> mappings = mappingDao.findAllByFromClass(className);
		List<LightMapping> ret = new ArrayList<LightMapping>();

		for (Mapping m : mappings) {
			LightMapping lm = new LightMapping();
			lm.setId(m.getId());
			lm.setFromClass(m.getFromClass());
			lm.setToClass(m.getToClass());
			lm.setLastUpdated(m.getLastUpdated());
			ret.add(lm);
		}
		return ret;
	}

	public CommonResponseBase create(Mapping mapping,
	        HttpServletResponse response) {
		mapping = mappingDao.save(mapping);
		CommonResponseBase ret = new CommonResponseBase();
		ret.setIdCreated(mapping.getId());
		return ret;
	}

	public String update(Long id, Mapping mapping, HttpServletResponse response) {

		if (mappingDao.equals(id)) {
			mappingDao.save(mapping);
			return "updated";
		}
		return id + "does not exist";
	}

	public String patch(Long id, String key, String value, String type,
	        String toType, Boolean remove, HttpServletResponse response) {
		if (!mappingDao.exists(id)) {
			return id + "does not exist";
		}
		Mapping mapping = mappingDao.getOne(id);
		List<Pair> list = mapping.getPairs();
		Iterator<Pair> i = list.iterator();
		HashMap<String, String> varNames = mapping.getVarNames();
		String ret = "Updated";
		if (remove) {
			while (i.hasNext()) {
				Pair p = i.next();
				if (p.getName().equals(key) && p.getToName().equals(value)) {
					i.remove();
					ret = "Removed";
					// remove varnames also
					mapping.getVarNames().remove(p.getToVarName());
					mapping.getVarNames().remove(p.getVarName());
				}
			}
		} else {
			boolean found = false;
			while (i.hasNext()) {
				Pair p = i.next();
				if (p.getName().equals(key) && !p.getToName().equals(value)) {
					p.setToName(value);
					p.setVarName(Util.handleDups(varNames, p.getFromVar(),
					        p.getName(), Util.FROM));
					String toParentVar = p.getToParentVar();
					if (toParentVar.equals("")) {
						toParentVar = mapping.getToClass().substring(
						        mapping.getToClass().lastIndexOf('.') + 1)
						        + "VO";
					}
					p.setToVarName(Util.handleDups(varNames, toParentVar,
					        Util.getParentId(p.getToName()), Util.TO));
					p.setType(type);
					p.setToType(toType);
					found = true;
					break;
				}
			}
			if (!found) {
				Pair p = new Pair();
				p.setName(key);
				p.setToName(value);
				p.setVarName(Util.handleDups(varNames, p.getFromVar(),
				        p.getName(), Util.FROM));
				String toParentVar = p.getToParentVar();
				if (toParentVar.length() == 0) {
					toParentVar = Util.getVarName(mapping.getToClass()
					        .substring(
					                mapping.getToClass().lastIndexOf('.') + 1)
					        + "VO");
				}
				toParentVar = Util.handleDups(varNames, toParentVar,
				        Util.getParentId(p.getToName()), Util.TO);
				System.out.println("toParentVar=" + toParentVar);
				p.setToVarName(toParentVar);
				p.setType(type);
				p.setToType(toType);
				list.add(p);
				ret = "Added";
			}
		}
		mappingDao.save(mapping);
		return ret;
	}

	public Mapping read(Long id, HttpServletResponse response) {
		return mappingDao.findOne(id);
	}

	public CommonResponseBase readUniqueFromClass(HttpServletResponse response) {
		Set<String> set = new HashSet<String>();
		List<Mapping> list = mappingDao.findAll();
		for (Mapping m : list) {
			set.add(m.getFromClass());
		}
		CommonResponseBase ret = new CommonResponseBase();
		ret.setSet(set);
		return ret;
	}

	public List<LightMapping> readAll(HttpServletResponse response) {
		List<LightMapping> ret = new ArrayList<LightMapping>();
		List<Mapping> list = mappingDao.findAll();
		for(Mapping m : list){
			LightMapping lm = new LightMapping();
			lm.setFromClass(m.getFromClass());
			lm.setToClass(m.getToClass());
			lm.setId(m.getId());
			lm.setPkg4Code(m.getPkg4Code());
			lm.setPkg4UT(m.getPkg4UT());
			ret.add(lm);
		}
		return ret;
	}

}
