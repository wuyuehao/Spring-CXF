package com.tony.mapinspector.rest;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DependencyRSImpl implements DependencyRS {

	private static String[] colors = new String[] { "#FFFFFF", "#FFFF99",
			"#FFCC33", "#CCFF33", "#66CCFF", "#3333FF", "#CC00FF" };

	public String inspect(String className, HttpServletResponse response) {
		Class c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return "class not found";
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


}
