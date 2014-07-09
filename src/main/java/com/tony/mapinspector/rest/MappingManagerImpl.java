package com.tony.mapinspector.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.tony.mapinspector.dao.MappingDao;
import com.tony.mapinspector.entity.LightMapping;
import com.tony.mapinspector.entity.Mapping;
import com.tony.mapinspector.entity.Pair;

public class MappingManagerImpl implements IMappingManager {

	@Autowired
	private MappingDao mappingDao;

	public List<LightMapping> readAll(String className,
			HttpServletResponse response) {
		List<Mapping> mappings = mappingDao.findAllByFromClass(className);
		List<LightMapping> ret = new ArrayList<LightMapping>();

		for (Mapping m : mappings) {
			LightMapping lm = new LightMapping();
			lm.setId(m.getId());
			lm.setLastUpdated(m.getLastUpdated());
			ret.add(lm);
		}
		return ret;
	}

	public void create(Mapping mapping, HttpServletResponse response) {
		mappingDao.save(mapping);
		return;
	}

	public String update(Long id, Mapping mapping, HttpServletResponse response) {

		if (mappingDao.equals(id)) {
			mappingDao.save(mapping);
			return "updated";
		}
		return id + "does not exist";
	}

	public String patch(Long id, String key, String value, String type, String toType, Boolean remove,
			HttpServletResponse response) {
		if (!mappingDao.exists(id)) {
			return id + "does not exist";
		}
		Mapping mapping = mappingDao.getOne(id);
		List<Pair> list = mapping.getPairs();
		Iterator<Pair> i = list.iterator();
		String ret = "Updated";
		if (remove) {
			while(i.hasNext()){
				Pair p = i.next();
				if(p.getName().equals(key) && p.getToName().equals(value)){
					i.remove();
					ret = "Removed";
				}
			}
		} else {
			boolean found = false;
			while(i.hasNext()){
				Pair p = i.next();
				if(p.getName().equals(key) && !p.getToName().equals(value)){
					p.setToName(value);
					p.setType(type);
					p.setToType(toType);
					found = true;
				}
			}
			if(!found){
				Pair p = new Pair();
				p.setName(key);
				p.setToName(value);
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

}
