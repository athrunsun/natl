package net.nitrogen.ates.testimporter;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarFileClassLoader extends URLClassLoader {
	public JarFileClassLoader(URL[] urls) {
		super(urls);
	}

	public void addFile(String path) throws MalformedURLException {
		String urlPath = "jar:file:/" + path + "!/";
		addURL(new URL(urlPath));
	}
}
