package com.codejavadoc;

import java.util.List;

import com.codejavadoc.util.MD5Util;


public class CodeSection {

	private List<String> rows;
	private String md5;

	public CodeSection(List<String> rows) {
		this.rows = rows;
		StringBuilder builder = new StringBuilder();
		for (String row : rows) {
			builder.append(row);
		}
		md5 = MD5Util.getMD5(builder.toString().replaceAll("\\s", ""));
	}

	public List<String> getRows() {
		return rows;
	}

	public String getMd5() {
		return md5;
	}
}
