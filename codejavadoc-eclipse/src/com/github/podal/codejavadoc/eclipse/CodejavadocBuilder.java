package com.github.podal.codejavadoc.eclipse;

import static com.github.podal.codejavadoc.eclipse.JavaProjectHelper.isOnClassPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.github.podal.codejavadoc.ClassInfoFetcher;
import com.github.podal.codejavadoc.CodeJavaDoc;
import com.github.podal.codejavadoc.CodeJavaDoc.HandleFile;
import com.github.podal.codejavadoc.JavaDocSection;
import com.github.podal.codejavadoc.util.FileUtil;
import com.github.podal.codejavadoc.util.FileUtil.Encoding;
import com.github.podal.codejavadoc.util.FileUtil.LineCallback;

public class CodejavadocBuilder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "com.github.podal.codejavadoc.CodejavadocBuilder";

	class ResourceVisitor implements IResourceDeltaVisitor, IResourceVisitor {
		private LineCallback callback;

		public ResourceVisitor(CodeJavaDocInfo info) {
			this(info.getCallback());
		}

		public ResourceVisitor(LineCallback callback) {
			this.callback = callback;
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
			case IResourceDelta.CHANGED:
				handle(resource);
				break;
			case IResourceDelta.REMOVED:
				handleDelete(resource);
				break;
			}
			return true;
		}

		@Override
		public boolean visit(IResource resource) {
			handle(resource);
			return true;
		}

		private void handle(IResource resource) {
			try {
				if (resource instanceof IFile && resource.getName().endsWith(".java")
						&& isOnClassPath(resource, getProject())) {
					File file = new File(resource.getRawLocation().toOSString());
					IFile file2 = (IFile) resource;
					fileMap.put(file, file2);
					FileUtil.file(file, encoding, callback);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void handleDelete(IResource resource) {
			// TODO fix delete
			handle(resource);
		}
	}

	private Map<File, IFile> fileMap = new HashMap<File, IFile>();
	private Encoding encoding = new Encoding() {

		@Override
		public String getEncoding(File file) {
			try {
				return fileMap.get(file).getCharset();
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
	};

	private HandleFile handleFile = new HandleFile() {
		@Override
		public void handleFile(File tmpDir, File file, File createdFile) {
			try {
				IFile resource = fileMap.get(file);
				resource.setContents(new FileInputStream(createdFile), IFile.KEEP_HISTORY, null);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	};

	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException {
		try {
			IResourceDelta delta;
			Activator activator = Activator.getDefault();
			Map<String, CodeJavaDocInfo> cache = activator.getCodeJavaDocCache();
			if (cache == null) {
				cache = activator.createCodeJavaDocCache();
			}
			File tmpDir = new File(Activator.getDefault().getStateLocation().toOSString());
			String projectName = getProject().getName();
			if (!cache.containsKey(projectName) || kind == FULL_BUILD || (delta = getDelta(getProject())) == null) {
				CodeJavaDocInfo info = new CodeJavaDocInfo();
				cache.put(projectName, info);
				final ResourceVisitor visitor = new ResourceVisitor(info);
				getProject().accept(visitor);
				CodeJavaDoc.updateJavaFiles(encoding, tmpDir, null, info.getCallback(), handleFile);
			} else {
				CodeJavaDocInfo info = cache.get(projectName);
				ClassInfoFetcher fetcher = new ClassInfoFetcher();
				delta.accept(new ResourceVisitor(new Dispatcher(fetcher, info.getCallback())));
				for (String method : fetcher.getVoidMethodMap().keySet()) {
					Set<File> files = info.getCallback().getIncludedMethods().get(method);
					if (files != null) {
						for (File file : files) {
							for (List<JavaDocSection> list : info.getCallback().getJavaDocMap().values()) {
								if (!list.isEmpty() && list.get(0).getFile().equals(file)) {
									CodeJavaDoc.doFile(encoding, tmpDir, null, handleFile, info.getCallback()
											.getIncludedMethodsCodeInfo(encoding), list);
								}
							}
						}

					}
				}

				for (Entry<String, List<JavaDocSection>> en : fetcher.getJavaDocMap().entrySet()) {
					CodeJavaDoc.doFile(encoding, tmpDir, null, handleFile, info.getCallback()
							.getIncludedMethodsCodeInfo(encoding), en.getValue());
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

class Dispatcher implements LineCallback {

	private LineCallback[] callbacks;

	public Dispatcher(LineCallback... callbacks) {
		this.callbacks = callbacks;
	}

	@Override
	public void line(File file, int lineCount, String line) {
		for (LineCallback callback : callbacks) {
			callback.line(file, lineCount, line);
		}
	}

}
