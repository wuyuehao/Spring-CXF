package com.tony.mapinspector.rest;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DependencyRSImpl implements DependencyRS {

	private static String[] colors = new String[] { "#FFFFFF", "#FFFF99", "#FFCC33", "#CCFF33", "#66CCFF",
			"#3333FF", "#CC00FF" };

	public String inspect(String className, HttpServletResponse response) {
		Class c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONArray ret = new JSONArray();
		try {
			traverse(c, ret, 0);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ret.toString();
	}

	private void traverse(Class c, JSONArray array, int dep)
			throws JSONException {
		for (Method method : c.getMethods()) {
			String name = method.getName();
			if (name.startsWith("set") && name.length() > 3) {
				Class parameter = method.getParameterTypes()[0];
				JSONObject o = new JSONObject();
				o.put("text", name + " type: " + parameter.getName());
				o.put("icon", "halflings-icon ok");
				o.put("backColor", colors[dep % colors.length]);
				if (parameter.getName().startsWith("com.paypal")) {
					if (parameter.getSuperclass().getName().contains("VOEnum")){
						//
					}else{
						JSONArray nested = new JSONArray();
						o.put("nodes", nested);
						traverse(parameter, nested, dep+1);
					}
				}
				array.put(o);
			}
		}
	}

}
