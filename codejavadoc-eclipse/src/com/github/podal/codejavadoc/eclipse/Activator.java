package com.github.podal.codejavadoc.eclipse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "codejavadoc-eclipse"; //$NON-NLS-1$

	private static Activator plugin;

	private Map<String, CodeJavaDocInfo> codeJavaDocCache;

	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public Map<String, CodeJavaDocInfo> getCodeJavaDocCache() {
		return codeJavaDocCache;
	}

	public Map<String, CodeJavaDocInfo> createCodeJavaDocCache() {
		return codeJavaDocCache = new HashMap<String, CodeJavaDocInfo>();
	}
}
