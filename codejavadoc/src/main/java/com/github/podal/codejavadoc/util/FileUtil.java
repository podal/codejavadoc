package com.github.podal.codejavadoc.util;

import static com.github.podal.codejavadoc.util.StringUtil.cutLast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {
	private FileUtil() {
	}

	public static interface LineCallback {
		public void line(File file, int lineCount, String line);
	}
	
	public static interface Encoding {
		String getEncoding(File file);
	}

	private static final FileFilter ALL_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return true;
		}
	};

	public static void file(File file, Encoding encoding, LineCallback callback) throws IOException {
		file(file, encoding, callback, ALL_FILE_FILTER);
	}

	public static void file(File file, Encoding encoding, LineCallback callback, FileFilter fileFilter)
			throws IOException {
		if (file.isFile()) {
			handleFile(file, encoding, callback);
		} else if (file.isDirectory()) {
			for (File file1 : file.listFiles(fileFilter)) {
				file(file1, encoding, callback, fileFilter);
			}
		}
	}

	private static void handleFile(File file, Encoding encoding, LineCallback callback) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding.getEncoding(file)));
		try {
			String line;
			for (int lineCount = 1; (line = reader.readLine()) != null; lineCount++) {
				callback.line(file, lineCount, line);
			}
		} finally {
			reader.close();
		}
	}

	public static String removeExstention(String fileName) {
		return cutLast(fileName, '.');
	}

}
