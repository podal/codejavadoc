package com.github.podal.codejavadoc;

import java.io.File;

public class JavaDocSection {

	private String className;
	private String includeClassName;
	private int end;
	private int start;
	private String md5 = null;
	private File file;

	public JavaDocSection(File file, String className, String includeClassName) {
		this.file = file;
		this.className = className;
		this.includeClassName = includeClassName;
	}

	public File getFile() {
		return file;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public String getMd5() {
		return md5;
	}

	public void setMD5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[file=").append(file);
		builder.append(", className=").append(className);
		builder.append(", includeClassName=").append(includeClassName);
		builder.append(", start=").append(start);
		builder.append(", end=").append(end);
		builder.append(", md5=").append(md5);
		return builder.append("]").toString();
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getIncludeClassName() {
		return includeClassName;
	}
}
