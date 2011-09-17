package com.github.podal.codejavadoc.eclipse;

import java.io.File;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.github.podal.codejavadoc.ClassInfoFetcher;

public class ClassInfoFetcherWithReset extends ClassInfoFetcher implements ResetLineCallback {

	@Override
	public void reset(File file) {
		String className = null;
		for (Entry<String, File> en : classNames.entrySet()) {
			if (en.getValue().equals(file)) {
				className = en.getKey();
				break;
			}
		}
		if (className != null) {
			blockId.remove(className);
			classNames.remove(className);
			javaDocMap.remove(className);

			Set<String> dels=new HashSet<String>();
			for (Entry<String, Integer> method : voidMethodMap.entrySet()) {
				if (method.getKey().startsWith(className)&&!method.getKey().substring(className.length()+1).contains(".")) {
					dels.add(method.getKey());
				} 
			}

			for(String del:dels) {
				voidMethodMap.remove(del);
			}

			
			if (section.getFile().equals(file)) {
				section = null;
			}
		}
	}

}
