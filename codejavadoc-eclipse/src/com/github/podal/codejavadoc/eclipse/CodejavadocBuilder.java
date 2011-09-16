package com.github.podal.codejavadoc.eclipse;

import static com.github.podal.codejavadoc.eclipse.JavaProjectHelper.isOnClassPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

import com.github.podal.codejavadoc.CodeJavaDoc;
import com.github.podal.codejavadoc.CodeJavaDoc.HandleFile;
import com.github.podal.codejavadoc.JavaDocSection;
import com.github.podal.codejavadoc.util.FileUtil;
import com.github.podal.codejavadoc.util.FileUtil.Encoding;

public class CodejavadocBuilder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "com.github.podal.codejavadoc.CodejavadocBuilder";

	class ResourceVisitor implements IResourceDeltaVisitor, IResourceVisitor {
		private ResetLineCallback callback;

		public ResourceVisitor(CodeJavaDocInfo info) {
			this(info.getCallback());
		}

		public ResourceVisitor(ResetLineCallback callback) {
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
					doFile(file, callback);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void handleDelete(IResource resource) {
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

	class CallbackHandleFile implements HandleFile {

		private ClassInfoFetcherWithReset callback;

		CallbackHandleFile(ClassInfoFetcherWithReset callback) {
			this.callback = callback;
		}

		@Override
		public void handleFile(File tmpDir, File file, File createdFile) {
			try {
				IFile resource = fileMap.get(file);
				doFile(file, callback);
				resource.setContents(new FileInputStream(createdFile), IFile.KEEP_HISTORY, null);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
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
				CodeJavaDoc.updateJavaFiles(encoding, tmpDir, null, info.getCallback(),
						new CallbackHandleFile(info.getCallback()));
			} else {
				CodeJavaDocInfo info = cache.get(projectName);
				ClassInfoFetcherWithReset fetcher = new ClassInfoFetcherWithReset();
				delta.accept(new ResourceVisitor(new Dispatcher(fetcher, info.getCallback())));
				Set<File> files = new HashSet<File>();
				for (String method : fetcher.getVoidMethodMap().keySet()) {
					if (info.getCallback().getIncludedMethods().containsKey(method)) {
						files.addAll(info.getCallback().getIncludedMethods().get(method));
					}
				}
				for (File file : files) {
					doFile(file, info.getCallback());					
					List<List<JavaDocSection>> list2 = new ArrayList<List<JavaDocSection>>(info.getCallback()
							.getJavaDocMap().values());
					for (List<JavaDocSection> list : list2) {
						if (!list.isEmpty() && list.get(0).getFile().equals(file)) {
							CodeJavaDoc.doFile(encoding, tmpDir, null, new CallbackHandleFile(info.getCallback()), info
									.getCallback().getIncludedMethodsCodeInfo(encoding), list);
						}
					}
				}
				for (Entry<String, List<JavaDocSection>> en : fetcher.getJavaDocMap().entrySet()) {
					CodeJavaDoc.doFile(encoding, tmpDir, null, new CallbackHandleFile(info.getCallback()), info
							.getCallback().getIncludedMethodsCodeInfo(encoding), en.getValue());
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void doFile(File file, ResetLineCallback callback) throws IOException {
		callback.reset(file);
		FileUtil.file(file, encoding, callback);
	}

}

class Dispatcher implements ResetLineCallback {

	private ResetLineCallback[] callbacks;

	public Dispatcher(ResetLineCallback... callbacks) {
		this.callbacks = callbacks;
	}

	@Override
	public void line(File file, int lineCount, String line) {
		for (ResetLineCallback callback : callbacks) {
			callback.line(file, lineCount, line);
		}
	}

	@Override
	public void reset(File file) {
		for (ResetLineCallback callback : callbacks) {
			callback.reset(file);
		}
	}

}
