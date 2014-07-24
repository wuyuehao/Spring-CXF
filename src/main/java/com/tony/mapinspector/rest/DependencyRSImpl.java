package com.tony.mapinspector.rest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.tony.mapinspector.dao.MappingDao;
import com.tony.mapinspector.entity.Mapping;
import com.tony.mapinspector.entity.Pair;

public class DependencyRSImpl implements DependencyRS {

	private static String[] colors = new String[] { "#FFFFFF", "#FFFF99",
			"#FFCC33", "#CCFF33", "#66CCFF", "#3333FF", "#CC00FF" };

	private static Logger log = Logger.getLogger(DependencyRSImpl.class);

	public String inspect(String className, HttpServletResponse response) {
		Class c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			response.setStatus(Response.SC_INTERNAL_SERVER_ERROR);
			return "{\"error\" : \"" + className + "class not found\"";
		}

		JSONArray ret = new JSONArray();
		try {
			traverse(c, ret, 0, "");
		} catch (JSONException e) {
			return e.getMessage();
		}
		return ret.toString();
	}

	private void traverse(Class c, JSONArray array, int dep, String prefix)
			throws JSONException {
		for (Method method : c.getMethods()) {
			String name = method.getName();
			if (name.startsWith("set") && name.length() > 3) {
				Class parameter = method.getParameterTypes()[0];
				JSONObject o = new JSONObject();
				name = name.substring(3);
				String text = name + " : " + parameter.getName();
				o.put("text", text);
				o.put("icon", "halflings-icon ok");
				o.put("backColor", colors[dep % colors.length]);
				JSONArray tags = new JSONArray();
				String tag = dep > 0 ? prefix + "." + name : name;
				tags.put(tag);
				o.put("tags", tags);
				if (parameter.getName().startsWith("com.paypal")) {
					if (parameter.getSuperclass().getName().contains("VOEnum")
							|| parameter.isEnum()) {
						// ignore enums
					} else {
						JSONArray nested = new JSONArray();
						o.put("nodes", nested);
						traverse(parameter, nested, dep + 1, tag);
					}
				}
				array.put(o);
			}
		}
	}

	@Autowired
	private MappingDao mappingDao;

	private Mapping mapping = null;

	public String read(String className, Long mapId,
			HttpServletResponse response) {
		if (mapId == 0) {
			List<Mapping> mappings = mappingDao.findAllByFromClass(className);
			for (Mapping m : mappings) {
				mapId = Math.max(mapId, m.getId());
			}
		}
		if (mapId == 0) {
			List<Mapping> mappings = mappingDao.findAllByToClass(className);
			for (Mapping m : mappings) {
				mapId = Math.max(mapId, m.getId());
			}
		}

		mapping = mappingDao.getOne(mapId);

		JSONObject obj = new JSONObject();
		if (mapping != null) {
			try {
				obj.put("to_class", mapping.getToClass());
				obj.put("from_class", mapping.getFromClass());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Class c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return "class not found";
		}

		JSONArray array = new JSONArray();
		try {
			traverse2(c, array, 0, "");
			obj.put("mapping", array);
		} catch (JSONException e) {
			return e.getMessage();
		}
		return obj.toString();
	}

	private void traverse2(Class c, JSONArray array, int dep, String prefix)
			throws JSONException {

		HashMap<String, Method> uniqueMethods = new HashMap<String, Method>();

		for (Method m : c.getMethods()) {
			if (m.getName().startsWith("set") && m.getName().length() > 3 && !m.getName().equals("setAdditionalProperties")) {
				if (uniqueMethods.containsKey(m.getName())) {
					if (uniqueMethods.get(m.getName()).getParameterTypes()[0]
							.getName().startsWith("java.")) {
						log.debug("visited_replaced="
								+ m.getName()
								+ ", type="
								+ uniqueMethods.get(m.getName())
										.getParameterTypes()[0].getName()
								+ "->" + m.getParameterTypes()[0].getName());
						uniqueMethods.put(m.getName(), m);
					}
				} else {
					uniqueMethods.put(m.getName(), m);
				}
			}
		}
		for (Method method : uniqueMethods.values()) {
			String name = method.getName();
			Class parameter = method.getParameterTypes()[0];
			JSONObject o = new JSONObject();
			name = name.substring(3);
			String type = parameter.getName();
			String tag = dep > 0 ? prefix + "." + name : name;
			o.put("name", tag);
			o.put("type", type);
			if (mapping != null) {

				List<Pair> list = mapping.getPairs();

				for (Pair p : list) {
					if (p.getName().equals(tag)) {
						o.put("mapto", p.getToName());
					}
					if (p.getToName().equals(tag)) {
						o.put("mapto", p.getName());
					}
				}
			}
			array.put(o);
			if (parameter.getName().startsWith("com.paypal") && !parameter.getName().endsWith("Currency")) { // skip Currency internals.
				traverse2(parameter, array, dep + 1, tag);
			}
		}
	}

}
