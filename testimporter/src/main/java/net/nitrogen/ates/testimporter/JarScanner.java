package net.nitrogen.ates.testimporter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.nitrogen.ates.core.entity.TestCase;
import net.nitrogen.ates.core.entity.TestGroup;

import org.testng.annotations.Test;

public class JarScanner {
	private static final String TEST_CLASS_TEST_METHOD_DELIMITER = ".";
	private static final String DEFAULT_TEST_CASE_ID = "N/A";

	private long projectId;
	private String jarFileWithoutDependenciesPath;
	private String jarFileWithDependenciesPath;

	/**
	 * @param projId
	 *            Test project id.
	 * @param jarFileWODependenciesPath
	 *            The full path of the jar w/o dependencies to scan.
	 */
	public JarScanner(long projId, String jarFileWODependenciesPath, String jarFileWDependenciesPath) {
		this.projectId = projId;
		this.jarFileWithoutDependenciesPath = jarFileWODependenciesPath;
		this.jarFileWithDependenciesPath = jarFileWDependenciesPath;

		// this.jarFileWithDependenciesPath = String.format(
		// "%s-jar-with-dependencies.jar", jarFileWODependenciesPath.substring(0, jarFileWODependenciesPath.length() - 4));
	}

	public void scan(List<TestCase> testCasesToReload, List<TestGroup> testGroupsToReload, Map<String, List<String>> rawTestGroupTestCasesToReload)
			throws ClassNotFoundException {
		this.scanJarWithDependencies(this.scanForClassNamesInJarWithoutDependencies(), testCasesToReload, testGroupsToReload, rawTestGroupTestCasesToReload);
	}

	private void scanJarWithDependencies(List<String> classNames, List<TestCase> testCasesToReload, List<TestGroup> testGroupsToReload,
			Map<String, List<String>> rawTestGroupTestCasesToReload) throws ClassNotFoundException {
		URL urls[] = {};
		JarFileClassLoader cl = new JarFileClassLoader(urls);

		try {
			cl.addFile(this.jarFileWithDependenciesPath);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		for (String className : classNames) {
			Method[] methods = cl.loadClass(className).getDeclaredMethods();

			if (methods != null && methods.length > 0) {
				for (Method method : methods) {
					if (method.isAnnotationPresent(Test.class)) {
						// Prepare test cases to reload
						Test testAnnotation = method.getAnnotation(Test.class);
						TestCase tc = new TestCase();
						tc.setProjectId(this.projectId);
						String testCaseName = String.format("%s%s%s", className, TEST_CLASS_TEST_METHOD_DELIMITER, method.getName());
						tc.setName(testCaseName);

						if (method.isAnnotationPresent(net.nitrogen.ates.testpartner.ATESTest.class)) {
							tc.setMappingId(method.getAnnotation(net.nitrogen.ates.testpartner.ATESTest.class).mappingId());
						}
						else {
							tc.setMappingId(DEFAULT_TEST_CASE_ID);
						}

						testCasesToReload.add(tc);

						// Prepare test groups and respective test names to reload
						for (String group : testAnnotation.groups()) {
							boolean exists = false;

							for (TestGroup g : testGroupsToReload) {
								if (g.getName().equals(group)) {
									exists = true;
									break;
								}
							}

							if (!exists) {
								TestGroup testGroup = new TestGroup();
								testGroup.setName(group);
								testGroup.setProjectId(this.projectId);
								testGroupsToReload.add(testGroup);
								List<String> testNames = new ArrayList<String>();
								testNames.add(testCaseName);
								rawTestGroupTestCasesToReload.put(group, testNames);
							}
							else {
								rawTestGroupTestCasesToReload.get(group).add(testCaseName);
							}
						}
					}
				}
			}
		}
	}

	private List<String> scanForClassNamesInJarWithoutDependencies() {
		List<String> classNames = new ArrayList<String>();
		List<String> relativeFilePaths = this.scanForRelativeFilePathsInJarWithoutDependencies();

		for (String path : relativeFilePaths) {
			String filePathWithoutSuffix = path.substring(0, path.length() - 5);
			String className = filePathWithoutSuffix.replace('/', '.');
			classNames.add(className);
		}

		return classNames;
	}

	private List<String> scanForRelativeFilePathsInJarWithoutDependencies() {
		List<String> relativeFilePaths = new ArrayList<String>();
		URL urls[] = {};
		JarFileClassLoader cl = new JarFileClassLoader(urls);

		try {
			cl.addFile(this.jarFileWithoutDependenciesPath);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		final URL[] resources = cl.getURLs();
		URLConnection connection;

		for (URL url : resources) {
			try {
				connection = url.openConnection();

				if (connection instanceof JarURLConnection) {
					final JarFile jarFile = ((JarURLConnection) connection).getJarFile();
					final Enumeration<JarEntry> entries = jarFile.entries();
					String filePath;

					for (JarEntry jarEntry = null; entries.hasMoreElements() && ((jarEntry = entries.nextElement()) != null);) {
						filePath = jarEntry.getName();

						// Skip inner classes
						if (filePath.contains(".class") && !filePath.contains("$")) {
							String filePathWithoutSuffix = filePath.substring(0, filePath.length() - 6);
							relativeFilePaths.add(String.format("%s.java", filePathWithoutSuffix));
						}
					}
				}
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return relativeFilePaths;
	}
}
