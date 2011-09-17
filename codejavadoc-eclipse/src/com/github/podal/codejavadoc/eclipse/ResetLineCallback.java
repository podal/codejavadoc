package com.github.podal.codejavadoc.eclipse;

import java.io.File;

import com.github.podal.codejavadoc.util.FileUtil.LineCallback;

public interface ResetLineCallback extends LineCallback {

	void reset(File file);
}
