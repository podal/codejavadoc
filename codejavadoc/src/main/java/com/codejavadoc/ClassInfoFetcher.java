package com.codejavadoc;

import static com.codejavadoc.CodeConstants.PATTERN_END;
import static com.codejavadoc.CodeConstants.PATTERN_START;
import static com.codejavadoc.CodeConstants.PATTERN_VOID_METHOD;
import static com.codejavadoc.CodeConstants.PATTERN_WITH_CHECK;
import static com.codejavadoc.util.FileUtil.file;
import static com.codejavadoc.util.FileUtil.removeExstention;
import static com.codejavadoc.util.MapUtil.findFirstKeyToValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.codejavadoc.util.FileUtil.Encoding;
import com.codejavadoc.util.FileUtil.LineCallback;

public class ClassInfoFetcher implements LineCallback {

	private Map<String, File> classNames = new HashMap<String, File>();
	private Map<String, List<JavaDocSection>> javaDocMap = new HashMap<String, List<JavaDocSection>>();
	private Map<String, Integer> voidMethodMap = new HashMap<String, Integer>();
	private Map<String, Set<File>> includedMethods = new HashMap<String, Set<File>>();

	private int blockId = 0;
	private JavaDocSection section;

	@Override
	public void line(File file, int lineCount, String line) {
		int i;
		Matcher matcher;
		if ((matcher = PATTERN_START.matcher(line)).matches()) {
			matchStartJavaDocString(file, lineCount, line, matcher);
		} else if ((matcher = PATTERN_END.matcher(line)).matches()) {
			matchEndJavaDocString(lineCount);
		} else if (matcher.matches()) {
			matchStartJavaDocString(file, lineCount, line, matcher);
		} else if ((matcher = PATTERN_VOID_METHOD.matcher(line)).matches()) {
			String className = findFirstKeyToValue(classNames, file);
			voidMethodMap.put(className + "." + matcher.group(1), lineCount);
		} else if (line.startsWith("package ") && (i = line.indexOf(';')) != -1) {
			String className = line.substring(8, i) + "." + removeExstention(file.getName());
			classNames.put(className, file);
		}
	}

	private void matchStartJavaDocString(File file, int lineCount, String line, Matcher matcher) {
		String className = findFirstKeyToValue(classNames, file);
		String name = matcher.group(1);
		Set<File> files = includedMethods.get(name);
		if (!includedMethods.containsKey(name)) {
			includedMethods.put(name, files = new HashSet<File>());
		}
		files.add(file);

		List<JavaDocSection> sections = javaDocMap.get(className);
		if (sections == null) {
			javaDocMap.put(className, sections = new ArrayList<JavaDocSection>());
		}
		if (blockId == sections.size()) {
			sections.add(section = new JavaDocSection(file, className, name));
		} else {
			section = sections.get(blockId);
		}
		section.setStart(lineCount);
		section.setMD5(getMD5(line));
	}

	private void matchEndJavaDocString(int lineCount) {
		if (section != null) {
			section.setEnd(lineCount);
			blockId++;
		}
	}

	private static String getMD5(String line) {
		Matcher matcher2 = PATTERN_WITH_CHECK.matcher(line);
		if (matcher2.matches()) {
			return matcher2.group(3);
		} else {
			return "";
		}
	}

	public Map<String, File> getClassNames() {
		return classNames;
	}

	public Map<String, List<JavaDocSection>> getJavaDocMap() {
		return javaDocMap;
	}

	public Map<String, Integer> getVoidMethodMap() {
		return voidMethodMap;
	}

	public Map<String, Set<File>> getIncludedMethods() {
		return includedMethods;
	}

	public Map<String, CodeSection> getIncludedMethodsCodeInfo(Encoding encoding) throws IOException {
		final Map<String, CodeSection> includeMethods = new HashMap<String, CodeSection>();
		for (String includeMethod : getIncludedMethods().keySet()) {
			Integer i = getVoidMethodMap().get(includeMethod);
			if (i != null) {
				FetchMethodCallback callback = new FetchMethodCallback(i);
				file(getClassNames().get(removeExstention(includeMethod)), encoding, callback);
				includeMethods.put(includeMethod, new CodeSection(callback.getRows()));
			}
		}
		return includeMethods;
	}

}
