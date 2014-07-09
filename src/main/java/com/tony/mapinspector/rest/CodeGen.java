package com.tony.mapinspector.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tony.mapinspector.dao.MappingDao;
import com.tony.mapinspector.entity.Mapping;
import com.tony.mapinspector.entity.Pair;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class CodeGen {

	@Autowired
	MappingDao mappingDao;
	HashSet<String> varNames = new HashSet<String>();
	
	private Logger log = Logger.getLogger(CodeGen.class);
	@GET
	@Path("codegen/{map_id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String gen(@PathParam("map_id") Long id,
			@Context ServletContext context) {
		
		varNames.clear(); // clear name spaces for every single call
		
		// freemarker init
		Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(context, "WEB-INF/templates");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setIncompatibleImprovements(new Version(2, 3, 20)); // FreeMarker
																// 2.3.20

		// meta data init
		Mapping mapping = mappingDao.findOne(id);
		String fromClass = mapping.getFromClass();
		String toClass = mapping.getToClass();

		List<Pair> pairs = mapping.getPairs();

		// create model
		HashMap<String, String> mapperModel = new HashMap<String, String>();

		// generate code for nested VOs
		Class clazz = null;
		try {
			clazz = Class.forName(toClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String nestedObjects = constructVOs(clazz, cfg);
		mapperModel.put("nestedObjects", nestedObjects);

		// construct methods

		Class fromClazz = null;
		try {
			fromClazz = Class.forName(fromClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String, Pair> map = new HashMap<String, Pair>();
		for (Pair p : pairs) {
			map.put(p.getName(), p);
		}

		StringWriter out = new StringWriter();
		mapMethods(fromClazz, map, cfg, out, "", clazz.getSimpleName());
		mapperModel.put("mappingMethods", out.getBuffer().toString());
		
		mapperModel.put("fromClass", fromClass);
		mapperModel.put("toClass", toClass);
		mapperModel.put("fromClassName", getSimpleName(fromClass));
		mapperModel.put("toClassName", getSimpleName(toClass));
		mapperModel.put("toClassVarName", getVarName(getSimpleName(toClass)));
		mapperModel.put("fromClassVarName", getVarName(getSimpleName(fromClass)));
		mapperModel.put("package", "pkg");
		
		out = new StringWriter();
		
		write(cfg, out, mapperModel, "mapper.ftl");

		/*
		 * String fromClassName = getSimpleName(fromClass); String toClassName =
		 * getSimpleName(toClass); for(Pair p : pairs){ String type =
		 * p.getType(); String name = p.getName(); String toName =
		 * p.getToName(); String toType = p.getToType(); String tempFile =
		 * "sameTypeMapping.ftl"; if(type.equals(toType)){ } HashMap<String,
		 * String> map = new HashMap<String, String>();
		 * map.put("toClassVarName", getVarName(getSimpleName(toName)));
		 * map.put("setMethodName", ""); map.put("fromClassVarName", "");
		 * map.put("getMethodName", ""); StringWriter out = new StringWriter();
		 * try { Template template = cfg.getTemplate(tempFile);
		 * template.process(map, out); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (TemplateException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * }
		 */

		return out.getBuffer().toString();

	}

	private void mapMethods(Class clazz, HashMap<String, Pair> pairs,
			Configuration cfg, StringWriter out, String key, String toClass) {

		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			String name = m.getName();
			Class type = m.getReturnType();
			if (name.startsWith("get") && name.length() > 3 && !name.equals("getClass")) {
				String className = type.getCanonicalName();
				String varName = getVarName(name.substring(3));
				varName = handleDups(varNames, varName);
				String parentVarName = getVarName(clazz.getSimpleName());
				String getMethodName = name;

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("className", className);
				map.put("varName", varName);
				map.put("parentVarName", parentVarName);
				map.put("getMethodName", getMethodName);

				String tempFile = "defineInput.ftl";
				write(cfg, out, map, tempFile);
				String myKey = key.length() == 0? name.substring(3) : key + "." + name.substring(3);
				if(pairs.containsKey(myKey)){
					// map and return;
					Pair p = pairs.get(myKey);
					String mappingTemp = "SameTypeMapping.ftl";
					String fromType = p.getType();
					String toType = p.getToType();
					String toName = p.getToName();
					boolean isFromEnum = false;
					try {
						isFromEnum = Class.forName(fromType).isEnum();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(!fromType.equals(toType)){
						if(fromType.equals("java.util.Date") && toType.equals("java.lang.Long")){
							mappingTemp = "Date2LongMapping.ftl";
						}else if(toType.equals("java.math.BigInteger") && fromType.equals("java.lang.String")){
							mappingTemp = "String2BigIntegerMapping.ftl";
						}else if(toType.equals("java.lang.Byte") && fromType.equals("java.lang.String")){
							mappingTemp = "String2ByteMapping.ftl";
						}else if(isFromEnum){
							mappingTemp = "Enum2StringMapping.ftl";
						}else{
							log.error("No Mapping Templates for : " + fromType + "->" +toType);
						}
					}
					String toClassVarName = getVarName(getParentName(toName, toClass));
					map.put("toClassVarName", toClassVarName);
					map.put("parameterName", toType);
					map.put("toClassSetMethodName", getSetterNameFromId(toName));
					write(cfg, out, map, mappingTemp);
				}else{
					Set<String> set = pairs.keySet();
					for(String s : set){
						if(s.startsWith(myKey)){
							// recursive if there is children in mapping table
							mapMethods(m.getReturnType(), pairs, cfg, out, myKey, toClass);
							break;
						}
					}
				}
				out.append("}\n");

			}
		}
	}

	private String getParentName(String toName, String defaultName) {
		if(!toName.contains(".")){
			return defaultName;
		}
		toName = toName.substring(0, toName.lastIndexOf('.'));
		if(!toName.contains(".")) return toName + "VO";
		return toName.substring(toName.lastIndexOf('.')+1) + "VO";
	}

	private String getSetterNameFromId(String toName) {
		if(!toName.contains(".")) return "set" + toName;
		int index = toName.lastIndexOf(".");
		String setter = toName.substring(index+1);
		setter = "set" + setter;
		return setter;
	}

	private String getGetterNameFromId(String toName) {
		if(!toName.contains(".")) return "get" + toName;
		int index = toName.lastIndexOf(".");
		String getter = toName.substring(0,index);
		String setter = toName.substring(index+1);
		getter = "get" + getter.replaceAll("\\.", "().get");
		setter = "set" + setter;
		return getter + "." + setter;
	}

	private void write(Configuration cfg, StringWriter out,
			HashMap<String, String> map, String tempFile) {
		Template template;
		try {
			template = cfg.getTemplate(tempFile);
			template.process(map, out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	private String getSimpleName(String toClass) {
		if (!toClass.contains("."))
			return toClass;
		return toClass.substring(toClass.lastIndexOf(".") + 1);
	}

	private String constructVOs(Class clazz, Configuration cfg) {

		StringWriter out = new StringWriter();

		ArrayDeque<Class> curr = new ArrayDeque<Class>();
		ArrayDeque<Class> next = new ArrayDeque<Class>();

		curr.offer(clazz);

		while (!curr.isEmpty()) {
			Class c = curr.poll();
			Method[] methods = c.getMethods();
			for (Method m : methods) {
				String name = m.getName();
				if (name.startsWith("set") && name.length() > 3) {
					Class parameter = m.getParameterTypes()[0];
					if (parameter.getName().endsWith("VO")) {

						String className = parameter.getName();
						String varName = getVarName(parameter.getSimpleName());
						varName = handleDups(varNames, varName);
						String parentVarName = getVarName(c.getSimpleName());
						String setMethodName = name;

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("className", className);
						map.put("varName", varName);
						map.put("parentVarName", parentVarName);
						map.put("setMethodName", setMethodName);

						String tempFile = "createVO.ftl";
						write(cfg, out, map, tempFile);
						next.offer(parameter);
					}
				}
			}
			if (curr.isEmpty()) {
				ArrayDeque<Class> tmp = curr;
				curr = next;
				next = tmp;
			}
		}
		return out.getBuffer().toString();
	}

	private String handleDups(HashSet<String> varNames, String varName) {
		while(varNames.contains(varName)){
			log.debug("Found a dup name: "+ varName);
			varName+="_";
		}
		varNames.add(varName);
		return varName;
	}

	private String getVarName(String simpleName) {
		return Character.toLowerCase(simpleName.charAt(0))
				+ simpleName.substring(1);
	}

}
