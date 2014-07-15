package com.tony.mapinspector.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
import org.springframework.beans.factory.annotation.Qualifier;

import com.tony.mapinspector.dao.MappingDao;
import com.tony.mapinspector.entity.Mapping;
import com.tony.mapinspector.entity.Pair;

import edu.emory.mathcs.backport.java.util.Arrays;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class CodeGen {

	@Autowired
	MappingDao mappingDao;
	ThreadLocal<HashSet<String>> threadLocal = new ThreadLocal<HashSet<String>>() {
		public HashSet<String> initialValue() {
			return new HashSet<String>();
		}
	};

	private Logger log = Logger.getLogger(CodeGen.class);

	@GET
	@Path("testgen/{map_id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String genUT(@PathParam("map_id") Long id,
	        @Context ServletContext context) {

		// clear name spaces for every single call
		threadLocal.get().clear();

		// create model
		HashMap<String, Object> mapperModel = new HashMap<String, Object>();

		// freemarker initialization
		Configuration cfg = freeMarkerInit(context);

		// metadata initilization
		Mapping mapping = mappingDao.findOne(id);
		String fromClass = mapping.getFromClass();
		String toClass = mapping.getToClass();
		List<Pair> pairs = mapping.getPairs();
		mapperModelInit(mapperModel, fromClass, toClass);

		mapperModel.put(
		        "nestedObjects",
		        constructNestedObjs(fromClass, cfg,
		                Util.getVarName(Util.getSimpleName(fromClass))));

		// feed values

		HashMap<String, String> feeder = new HashMap<String, String>();
		int count = 0;
		for (Pair p : pairs) {
			String key = Util.getSetterById(p.getName());
			String type = p.getType();
			String value = null;
			if (type.equals("java.util.Date")) {
				value = "new java.util.Date(" + Util.nextLong(4294967295L)
				        + "L)";
			} else if (type.equals("java.lang.String")) {
				value = "\"" + Integer.toString(count) + "\"";
			} else if (type.equals("java.lang.Boolean")) {
				value = "true";
			} else if (type.equals("java.util.List")) {
				value = "new ArrayList(Arrays.asList(\"" + count + "\"))";
			} else {
				try {
					Class c = Class.forName(type);
					if (c.isEnum()) {
						value = type.replaceAll("\\$", ".") + "."
						        + c.getEnumConstants()[0].toString();
					} else {
						log.error("Not able to handle type = " + type);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			feeder.put(key, value);
			count++;
		}
		mapperModel.put("feeder", feeder);

		// construct mappings
		HashMap<String, String> assertions = new HashMap<String, String>();

		for (Pair p : pairs) {
			String key = Util.getGetterById(p.getName());
			String value = Util.getGetterById(p.getToName());
			String type = p.getType();
			try {
	            if(Class.forName(type).isEnum()){
	            	key = key + "().toString()";
	            	value = value + "AsEnum().getName()";
	            }else{
	            	key = key + "()";
	            	value = value + "()";
	            }
            } catch (ClassNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
			
			assertions.put(key, value);
		}
		mapperModel.put("assertions", assertions);

		StringWriter out = new StringWriter();
		write(cfg, out, mapperModel, "UTmapper.ftl");

		return out.getBuffer().toString();

	}

	private String constructNestedObjs(String fromClass, Configuration cfg,
	        String parentName) {
		Class c = null;

		try {
			c = Class.forName(fromClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringWriter out = new StringWriter();

		rec(cfg, parentName, c, out);

		return out.getBuffer().toString();
	}

	private void rec(Configuration cfg, String parentName, Class c,
	        StringWriter out) {
		Method[] methods = c.getMethods();
		Arrays.sort(methods, new MethodComparator());
		for (Method m : methods) {
			String methodName = m.getName();
			if (isValidSetMethod(methodName)) {
				Class parameter = m.getParameterTypes()[0];
				if(!parameter.getName().startsWith("com.paypal") || parameter.isEnum()){
					continue;
				}
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("class_name", parameter.getName());
				String varName =  Util.getVarName(parameter.getSimpleName());
				varName = Util.handleDups(threadLocal.get(), varName);
				map.put("var_name", varName);
				map.put("parent_name", parentName);
				map.put("setter", methodName);
				write(cfg, out, map, "createVO.ftl");
				rec(cfg, varName, parameter, out);
			}
		}
	}

	@GET
	@Path("codegen/{map_id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String gen(@PathParam("map_id") Long id,
	        @Context ServletContext context) {

		// clear name spaces for every single call
		threadLocal.get().clear();

		// create model
		HashMap<String, Object> mapperModel = new HashMap<String, Object>();

		// freemarker initialization
		Configuration cfg = freeMarkerInit(context);

		// metadata initilization
		Mapping mapping = mappingDao.findOne(id);
		String fromClass = mapping.getFromClass();
		String toClass = mapping.getToClass();
		List<Pair> pairs = mapping.getPairs();
		mapperModelInit(mapperModel, fromClass, toClass);

		// generate code for nested VOs
		Class c = null;
		try {
			c = Class.forName(toClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringWriter out = new StringWriter();
		constructVOs(c, cfg, out, mapping, "", mapping.getToClassVarName()
		        + "VO");
		mapperModel.put("nestedObjects", out.getBuffer().toString());

		// construct mappings
		out = constructMappings(cfg, fromClass, pairs);
		mapperModel.put("mappingMethods", out.getBuffer().toString());

		// combine the result
		out = new StringWriter();
		write(cfg, out, mapperModel, "mapper.ftl");

		// persist for the varNames
		mappingDao.save(mapping);

		return out.getBuffer().toString();

	}

	@GET
	@Path("codegen/sandbox/{map_id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String sandbox(@PathParam("map_id") Long id,
	        @Context ServletContext context) {

		// clear name spaces for every single call
		threadLocal.get().clear();

		// create model
		HashMap<String, Object> mapperModel = new HashMap<String, Object>();

		// freemarker initialization
		Configuration cfg = freeMarkerInit(context);

		// metadata initilization
		Mapping mapping = mappingDao.findOne(id);
		String fromClass = mapping.getFromClass();
		String toClass = mapping.getToClass();
		List<Pair> pairs = mapping.getPairs();
		mapperModelInit(mapperModel, fromClass, toClass);

		StringWriter out = new StringWriter();

		generateMappings(toClass, cfg, mapping, "", out);

		return out.getBuffer().toString();

	}

	private void mapperModelInit(HashMap<String, Object> mapperModel,
	        String fromClass, String toClass) {
		mapperModel.put("fromClass", fromClass);
		mapperModel.put("toClass", toClass);
		mapperModel.put("fromClassName", getSimpleName(fromClass));
		mapperModel.put("toClassName", getSimpleName(toClass));
		mapperModel.put("toClassVarName", getVarName(getSimpleName(toClass))
		        + "VO");
		mapperModel.put("fromClassVarName",
		        getVarName(getSimpleName(fromClass)));
		mapperModel.put("package", props.get("package"));
	}

	private StringWriter constructMappings(Configuration cfg, String fromClass,
	        List<Pair> pairs) {
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
		mapMethodsRec(fromClazz, map, cfg, out, "",
		        getVarName(fromClazz.getSimpleName()));
		return out;
	}

	private Configuration freeMarkerInit(ServletContext context) {
		// freemarker init
		Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(context, "WEB-INF/templates");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setIncompatibleImprovements(new Version(2, 3, 20)); // FreeMarker
		                                                        // 2.3.20
		return cfg;
	}

	private class MethodComparator implements Comparator<Method> {

		public int compare(Method m1, Method m2) {
			return m1.getName().compareTo(m2.getName());
		}

	}

	private void constructVOs(Class c, Configuration cfg, StringWriter out,
	        Mapping mapping, String idPrefix, String parentVar) {
		Method[] methods = c.getMethods();
		Arrays.sort(methods, new MethodComparator());
		for (Method m : methods) {
			String methodName = m.getName();
			if (isValidSetMethod(methodName)) {
				Class parameter = m.getParameterTypes()[0];
				String id = idPrefix.equals("") ? methodName.substring(3)
				        : idPrefix + "." + methodName.substring(3);
				Pair p = mapping.getFirstDirectChildrenOfToId(id);
				boolean isDirectChild = true;
				if (p == null) {
					isDirectChild = false;
					p = mapping.getFirstChildrenOfToId(id);
				}
				if (p != null) {
					String className = parameter.getName();
					String varName = isDirectChild ? p.getToParentVar() : Util
					        .handleDups(mapping.getVarNames(),
					                getVarName(parameter.getSimpleName()), id,
					                Util.TO);
					String parentVarName = parentVar;
					String setMethodName = methodName;

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("class_name", className);
					map.put("var_name", varName);
					map.put("parent_name", parentVarName);
					map.put("setter", setMethodName);

					String tempFile = "createVO.ftl";
					write(cfg, out, map, tempFile);
					constructVOs(parameter, cfg, out, mapping, id, varName);
				}
			}
		}
	}

	private void mapMethodsRec(Class clazz, HashMap<String, Pair> pairs,
	        Configuration cfg, StringWriter out, String key, String parentName) {

		Method[] methods = clazz.getMethods();

		Arrays.sort(methods, new MethodComparator());

		for (Method m : methods) {
			String methodName = m.getName();
			if (isValidSetMethod(methodName)) {
				Class paramenter = m.getParameterTypes()[0];
				String className = paramenter.getCanonicalName();
				String varName = getVarName(methodName.substring(3));
				// varName = handleDups(threadLocal.get(), varName);
				String parentVarName = parentName;
				String getMethodName = "get" + methodName.substring(3);

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("className", className);
				map.put("varName", varName);
				map.put("parentVarName", parentVarName);
				map.put("getMethodName", getMethodName);

				String tempFile = "defineInput.ftl";
				write(cfg, out, map, tempFile);
				String myKey = key.length() == 0 ? methodName.substring(3)
				        : key + "." + methodName.substring(3);
				if (pairs.containsKey(myKey)) {
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

					if (!fromType.equals(toType)) {
						if (fromType.equals("java.util.Date")
						        && toType.equals("java.lang.Long")) {
							mappingTemp = "Date2LongMapping.ftl";
						} else if (toType.equals("java.math.BigInteger")
						        && fromType.equals("java.lang.String")) {
							mappingTemp = "String2BigIntegerMapping.ftl";
						} else if (toType.equals("java.lang.Byte")
						        && fromType.equals("java.lang.String")) {
							mappingTemp = "String2ByteMapping.ftl";
						} else if (isFromEnum) {
							mappingTemp = "Enum2StringMapping.ftl";
						} else if (toType.equals("com.paypal.types.Currency")
						        && fromType
						                .equals("com.paypal.api.platform.riskprofileapi.Currency")) {
							mappingTemp = "Currency2CurrencyMapping.ftl";
						} else if (toType.equals("java.lang.Long")
						        && fromType.equals("java.lang.Integer")) {
							mappingTemp = "Integer2LongMapping.ftl";
						} else if (toType.equals("java.lang.Long")
						        && fromType.equals("java.lang.String")) {
							mappingTemp = "String2LongMapping.ftl";
						} else {
							log.error("No Mapping Templates for : " + fromType
							        + "->" + toType);
						}
					}
					String toClassVarName = p.getToVarName();
					// toClassVarName = handleSpecialCase(toClassVarName);
					map.put("toClassVarName", toClassVarName);
					map.put("parameterName", toType);
					map.put("toClassSetMethodName", getSetterNameFromId(toName));
					write(cfg, out, map, mappingTemp);
				} else {
					Set<String> set = pairs.keySet();
					for (String s : set) {
						if (s.startsWith(myKey)) {
							// recursive if there is children in mapping table
							mapMethodsRec(paramenter, pairs, cfg, out, myKey,
							        varName);
							break;
						}
					}
				}
				out.append("}\n");

			}
		}
	}

	@Autowired
	@Qualifier("codeGenProps")
	private HashMap<String, String> props;

	private String handleSpecialCase(String name) {
		if (props.containsKey(name)) {
			return props.get(name);
		}
		return name;
	}

	private String normalizeVOName(String name) {
		int count = 0;
		while (name.endsWith("_")) {
			name = name.substring(0, name.length() - 1);
			count++;
		}
		if (!name.endsWith("VO")) {
			name += "VO";
		}
		while (count-- > 0) {
			name += "_";
		}
		return getVarName(name);
	}

	private String getParentName(String toName, String defaultName) {
		if (!toName.contains(".")) {
			return defaultName;
		}
		toName = toName.substring(0, toName.lastIndexOf('.'));
		if (!toName.contains("."))
			return toName + "VO";
		return toName.substring(toName.lastIndexOf('.') + 1) + "VO";
	}

	private String getSetterNameFromId(String toName) {
		if (!toName.contains("."))
			return "set" + toName;
		int index = toName.lastIndexOf(".");
		String setter = toName.substring(index + 1);
		setter = "set" + setter;
		return setter;
	}

	private String getGetterFromId(String id) {
		return "get" + id.replaceAll("\\.", "().get");
	}

	private void write(Configuration cfg, StringWriter out, HashMap map,
	        String tempFile) {
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

	private String getVarFromId(String id) {
		return getVarName(id.substring(id.lastIndexOf('.') + 1));
	}

	private void writeBeforeMapping(Configuration cfg, StringWriter out,
	        Class parameter, String parent, String varName, String setter) {
		String type = parameter.getName();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("class_type", type);
		map.put("parent_var", parent);
		map.put("var_name", varName);
		map.put("setter", setter);

		// ${class_type} ${var_name} = new ${class_type}();
		// ${parent_var}.${setter}(${var_name});

		String template = "Mapping_Before.ftl";
		write(cfg, out, map, template);
	}

	private void writeMapping(Configuration cfg, StringWriter out, Pair pair) {

		String fromVar = pair.getFromVar();
		String toVar = pair.getToParentVar();
		String fromType = pair.getType();
		String toType = pair.getToType();

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("to_parent_var", pair.getVarName());
		map.put("from_parent_var", pair.getVarName());

		map.put("setter", pair.getSetter());
		map.put("getter", pair.getGetter());

		boolean isFromEnum = false;
		try {
			isFromEnum = Class.forName(fromType).isEnum();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String template = "Mapping_SameType.ftl";
		// if (!fromType.equals(toType)) {
		// if (fromType.equals("java.util.Date")
		// && toType.equals("java.lang.Long")) {
		// template = "Mapping_Date2Long.ftl";
		// } else if (toType.equals("java.math.BigInteger")
		// && fromType.equals("java.lang.String")) {
		// template = "Mapping_String2BigInteger.ftl";
		// } else if (toType.equals("java.lang.Byte")
		// && fromType.equals("java.lang.String")) {
		// template = "Mapping_String2Byte.ftl";
		// } else if (isFromEnum) {
		// template = "Mapping_Enum2String.ftl";
		// } else if (toType.equals("com.paypal.types.Currency")
		// && fromType
		// .equals("com.paypal.api.platform.riskprofileapi.Currency")) {
		// template = "Mapping_Currency2Currency.ftl";
		// } else if (toType.equals("java.lang.Long")
		// && fromType.equals("java.lang.Integer")) {
		// template = "Mapping_Integer2Long.ftl";
		// } else if (toType.equals("java.lang.Long")
		// && fromType.equals("java.lang.String")) {
		// template = "Mapping_String2Long.ftl";
		// } else {
		// log.error("No Mapping Templates for : " + fromType + "->"
		// + toType);
		// }
		// }
		write(cfg, out, map, template);
	}

	private boolean isValidSetMethod(String methodName) {
		return methodName.startsWith("set") && methodName.length() > 3
		        && !methodName.equals("setAdditionalProperties");
	}

	private Method[] getSortedMethods(String toClass) {
		Class clazz = null;
		try {
			clazz = Class.forName(toClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Method[] methods = clazz.getMethods();
		Arrays.sort(methods, new MethodComparator());
		return methods;
	}

	private void generateMappings(String toClass, Configuration cfg,
	        Mapping mapping, String idPrefix, StringWriter out) {
		for (Method m : getSortedMethods(toClass)) {
			String methodName = m.getName();
			if (isValidSetMethod(methodName)) {
				Class parameter = m.getParameterTypes()[0];
				String id = idPrefix.equals("") ? methodName.substring(3)
				        : idPrefix + "." + methodName.substring(3);
				if (mapping.containsToId(id)) {
					Pair pair = mapping.getPairByToName(id);
					writeMapping(cfg, out, pair);
				} else if (mapping.containsChildrenOfToId(id)) {
					// String toVarName = handleDups(threadLocal.get(),
					// getVarFromId(id) + "VO");
					// String fromVarName = handleDups(threadLocal.get(),
					// getVarFromId(id));
					// String setter = "set"
					// + id.substring(id.lastIndexOf('.') + 1);
					// writeBeforeMapping(cfg, out, parameter, fromVarName,
					// toVarName, setter);
					// generateMappings(parameter.getName(), cfg, mapping, id,
					// out);
				}
			}
		}
	}

	private String getVarName(String simpleName) {
		return Character.toLowerCase(simpleName.charAt(0))
		        + simpleName.substring(1);
	}

}
