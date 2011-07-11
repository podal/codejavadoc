package com.github.podal.codejavadoc.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.core.JavaProject;

@SuppressWarnings("restriction")
public class JavaProjectHelper {
	private JavaProjectHelper() {
	}

	public static boolean isOnClassPath(IResource resource, IProject project) {
		JavaProject retProject = getJavaProject(project);
		return retProject == null ? false : retProject.isOnClasspath(resource);
	}

	public static JavaProject getJavaProject(IProject project) {
		try {
			JavaProject retProject = null;
			IProjectNature p = project.getNature("org.eclipse.jdt.core.javanature");
			if (p != null) {
				retProject = (JavaProject) p;
			}
			return retProject;
		} catch (CoreException e) {
			return null;
		}
	}

}
