package com.tony.mapinspector.rest;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.tony.mapinspector.dao.MappingDao;
import com.tony.mapinspector.entity.Mapping;

public class MappingManagerImpl implements IMappingManager {

	@Autowired
	private MappingDao mappingDao;

	public Mapping read(String id, HttpServletResponse response) {
		return mappingDao.findByUid(id);
	}

	private JSONObject makeJSONObject(String l, String r) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("key", l);
			obj.put("value", r);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public void create(Mapping mapping, HttpServletResponse response) {
		mappingDao.save(mapping);
		return;
	}

}
