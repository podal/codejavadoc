package com.codejavadoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.codejavadoc.util.FileUtil.LineCallback;


public class FetchMethodCallback implements LineCallback {

	private int row;
	private List<String> rows = new ArrayList<String>();

	private int ply = 0;
	private int startPly = -1;

	public FetchMethodCallback(int row) {
		this.row = row;
	}

	public List<String> getRows() {
		return rows;
	}

	@Override
	public void line(File file, int lineCount, String line) {
		for (int i = 0; (i = line.indexOf('{', i + 1)) != -1;) {
			ply++;
		}
		for (int i = 0; (i = line.indexOf('}', i + 1)) != -1;) {
			ply--;
		}
		if (ply == startPly) {
			startPly = -1;
		} else if (startPly != -1) {
			rows.add(line);
		} else if (lineCount == row) {
			startPly = ply - 1;
		}

	}
}
