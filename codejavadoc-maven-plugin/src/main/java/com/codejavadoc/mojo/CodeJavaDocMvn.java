package com.codejavadoc.mojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.codejavadoc.CodeJavaDoc;
import com.codejavadoc.CodeJavaDoc.Log;
import com.codejavadoc.util.FileUtil.Encoding;

/**
 * @goal codejavadoc
 */
public class CodeJavaDocMvn extends AbstractMojo {
	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		try {
			List<String> list = new ArrayList<String>();
			MavenProject project = getProject();
			list.addAll(project.getCompileSourceRoots());
			list.addAll(project.getTestCompileSourceRoots());
			List<File> fileList = new ArrayList<File>();
			for (String fileName : list) {
				File file = new File(fileName);
				if (file.exists() && file.isDirectory()) {
					fileList.add(file);
				}
			}
			if (!fileList.isEmpty()) {
				File tmpFile = new File(project.getBasedir() + "/target/codeToJavaDoc");
				if (!tmpFile.exists()) {
					if (!tmpFile.mkdirs()) {
						throw new MojoExecutionException("Can't create dir " + tmpFile);
					}
				}
				final String encoding = (String) project.getProperties().get("project.build.sourceEncoding");
				if (encoding == null) {
					getLog().error("Must set project.build.sourceEncoding");
					throw new MojoExecutionException("Must set project.build.sourceEncoding");
				}
				CodeJavaDoc.doCodeToJavaDoc(fileList.toArray(new File[0]), new Encoding() {

					@Override
					public String getEncoding(File file) {
						return encoding;
					}
				}, tmpFile, new Log() {
					@Override
					public void log(String line) {
						getLog().debug(line);
					}
				});
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Error execute", e);
		}
	}

	public MavenProject getProject() {
		return (MavenProject) getPluginContext().get("project");
	}
}