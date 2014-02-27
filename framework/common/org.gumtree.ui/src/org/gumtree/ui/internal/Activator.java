package org.gumtree.ui.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.gumtree.ui.util.resource.SharedImage;
import org.gumtree.util.eclipse.E4Utils;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	public static final String PLUGIN_ID = "org.gumtree.ui";

	private static BundleContext context;

	private static Activator instance;
	
	private volatile IEclipseContext eclipseContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		instance = this;
		context = bundleContext;
//		java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.SEVERE);
//		java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.SEVERE);
//		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		if (eclipseContext != null) {
			eclipseContext.dispose();
			eclipseContext = null;	
		}
		if (InternalImage.isInstalled()) {
			InternalImage.dispose();
		}
		if (SharedImage.isInstalled()) {
			SharedImage.dispose();
		}
		context = null;
		instance = null;
		super.stop(bundleContext);
	}

	public IEclipseContext getEclipseContext() {
		if (eclipseContext == null) {
			synchronized (this) {
				if (eclipseContext == null) {
					eclipseContext = E4Utils.createEclipseContext(getContext());
				}
			}
		}
		return eclipseContext;
	}
	
	public static BundleContext getContext() {
		return context;
	}

	public static Activator getDefault() {
		return instance;
	}

}
