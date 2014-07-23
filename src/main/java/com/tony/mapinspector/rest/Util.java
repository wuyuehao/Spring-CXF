package com.tony.mapinspector.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class Util {

	public static final String FROM = "FROM";
	public static final String TO = "TO";

	private static Logger log = Logger.getLogger(Util.class);

	public static String handleDups(HashSet<String> varNames, String varName) {
		while (varNames.contains(varName)) {
			log.debug("Found a dup name: " + varName);
			varName += "_";
		}
		varNames.add(varName);
		return varName;
	}

	public static String handleDups(HashMap<String, String> varNames,
	        String varName, String id, String prefix) {

		id = prefix + id;

		if (varNames.containsKey(id)) {
			return varNames.get(id);
		}

		while (varNames.values().contains(varName)) {
			log.debug("Found a dup name: " + varName);
			varName += "_";
		}
		varNames.put(id, varName);
		return varName;
	}

	public static String getVarName(String simpleName) {
		return Character.toLowerCase(simpleName.charAt(0))
		        + simpleName.substring(1);
	}

	public static String getParentId(String id) {
		String parentId = null;
		if (!id.contains(".")) {
			parentId = "";
		} else {
			parentId = id.substring(0, id.lastIndexOf('.'));
		}
		return parentId;
	}

	public static String getGetterById(String id) {
		return "get" + id.replaceAll("\\.", "().get");
	}

	public static String getSetterById(String id) {
		if (!id.contains("."))
			return "set" + id;
		int index = id.lastIndexOf(".");
		String getter = id.substring(0, index);
		String setter = id.substring(index + 1);
		getter = "get" + getter.replaceAll("\\.", "().get");
		setter = "set" + setter;
		return getter + "()." + setter;
	}

	public static String getSimpleName(String name) {
		if (!name.contains("."))
			return name;
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public static String nextLong(long n) {
		Random rng = new Random();
		// error checking and 2^x checking removed for simplicity.
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return Long.toString(val);
	}

	public static String formatCode(String code){
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
 
		TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);
		IDocument doc = new Document(code);
		try {
			textEdit.apply(doc);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return doc.get();
	}
}
