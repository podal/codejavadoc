package com.codejavadoc.util;

import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {
	private MapUtil() {
	}

	public static <K, V> K findFirstKeyToValue(Map<K, V> map, V value) {
		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

}
