package org.gumtree.widgets.swt.util;

import java.net.URI;
import java.net.URL;

import javax.swing.ImageIcon;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.gumtree.widgets.internal.Activator;

public enum SharedImages {

	BG_TEXTURED("images/NSTexturedFullScreenBackgroundColor.png");

	private SharedImages(String path) {
		this.path = path;
	}

	public Image getImage() {
		return getRegistry().get(name());
	}

	public ImageDescriptor getDescriptor() {
		return getRegistry().getDescriptor(name());
	}

	public URI getURI() {
		return URI.create("bundle://" + Activator.PLUGIN_ID + "/" + path());
	}

	public ImageIcon createImageIcon() {
		java.net.URL imgURL = Activator.class.getResource(path());
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			return null;
		}
	}

	public static boolean isInstalled() {
		return registry != null;
	}

	public static synchronized void dispose() {
		if (registry != null) {
			registry.dispose();
			registry = null;
		}
	}

	private String path() {
		return path;
	}

	private static ImageRegistry getRegistry() {
		if (registry == null) {
			synchronized (SharedImages.class) {
				if (registry == null) {
					registry = new ImageRegistry(Display.getDefault());
					for (SharedImages key : values()) {
						if (UIResourceUtils.isOsgiRunning()) {
							// Under OSGi runtime
							URL fullPathString = UIResourceUtils.findFile(
									Activator.PLUGIN_ID, key.path());
							registry.put(key.name(), ImageDescriptor
									.createFromURL(fullPathString));
						} else {
							// Under normal Java runtime
							registry.put(key.name(), ImageDescriptor
									.createFromFile(Activator.class,
											"../../../../" + key.path()));
						}
					}
				}
			}
		}
		return registry;
	}

	private String path;

	private static volatile ImageRegistry registry;

}
