package com.codejavadoc;

import java.util.regex.Pattern;

public interface CodeConstants {
	String START_STRING = "<!--Code start[%s] [%s]-->\n";
	String END_STRING = "<!--Code end-->\n";
	Pattern PATTERN_START = Pattern.compile(".*<!--\\s*Code start\\[([a-zA-Z0-9\\.]*)\\].*-->.*");
	Pattern PATTERN_END = Pattern.compile(".*<!--\\s*Code end\\s*-->.*");
	Pattern PATTERN_WITH_CHECK = Pattern
			.compile(".*<!--\\s*Code (start)\\[([a-zA-Z0-9\\.]*)\\].*\\[([A-F0-9]*)\\].*-->.*");
	Pattern PATTERN_VOID_METHOD = Pattern.compile(".*void (.*)\\(\\).*\\{.*");

}
