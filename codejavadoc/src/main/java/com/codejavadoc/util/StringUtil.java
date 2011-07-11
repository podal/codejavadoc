package com.codejavadoc.util;

import java.util.Arrays;

public class StringUtil {
	private StringUtil() {
	}

	public static String cutLast(String fileName, char ch) {
		return fileName.substring(0, fileName.lastIndexOf(ch));
	}

	public static String padLeft(String string, char c, int len) {
		char[] chs = new char[len];
		Arrays.fill(chs, c);
		String string2 = new String(chs) + string;
		return string2.substring(string2.length() - len);
	}
}
