package net.nitrogen.ates.testimporter;

import net.nitrogen.ates.core.entity.TestCase;
import net.nitrogen.ates.core.entity.TestGroup;
import org.junit.Assert;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Test(groups = "atesonly")
public class DummyTestCase {
	private static final String TEST_CLASS_TEST_METHOD_DELIMITER = ".";
	private static final String DEFAULT_TEST_CASE_ID = "N/A";
	private static final long DEFAULT_PROJECT_ID = 1;
	private static final String projectIDProperty = "nitrogen_ates_projectid";

	private static List<TestCase> testCasesToReload = new ArrayList<TestCase>();
	private static List<TestGroup> testGroupsToReload = new ArrayList<TestGroup>();
	private static Map<String, List<String>> rawTestGroupTestCasesToReload = new HashMap<String, List<String>>();

	@BeforeSuite
	public void scan(ITestContext context) {
		final String projectIDSaved = System.getProperty(projectIDProperty);

		if(projectIDSaved == null) {
			throw new RuntimeException(String.format("Project ID property is null!"));
		}

		long projectId = Long.parseLong(projectIDSaved);

		readTestCaseAndGroups(projectId, context);
		new Importer(projectId).doImport(testCasesToReload, testGroupsToReload, rawTestGroupTestCasesToReload);

		// Once it's done, quit the execution by failing the suite.
		// As a result, all of the test cases will be skipped.
		Assert.assertTrue(false);
	}

	private void readTestCaseAndGroups(long projectId, ITestContext context) {
		final ITestNGMethod[] allTestMethods = context.getAllTestMethods();
		for (ITestNGMethod method : allTestMethods) {
			readTestCase(projectId, method);
			readTestGroups(projectId, method);
		}
	}

	private void readTestGroups(long projectId, ITestNGMethod method) {
		// Prepare test groups and respective test names to reload
		String className = method.toString().replaceAll(".*instance:", "").replaceAll("@.*", "");
		String testCaseName = String.format("%s%s%s", className, TEST_CLASS_TEST_METHOD_DELIMITER, method.getMethodName());

		for (String group : method.getGroups()) {
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
				testGroup.setProjectId(projectId);
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

	private void readTestCase(long projectId, ITestNGMethod method) {
		TestCase tc = new TestCase();
		String className = method.toString().replaceAll(".*instance:", "").replaceAll("@.*", "");
		String testCaseName = String.format("%s%s%s", className, TEST_CLASS_TEST_METHOD_DELIMITER, method.getMethodName());

		for (TestCase testcase : testCasesToReload) {
			if (testcase.getName().equals(testCaseName)) {
				return;
			}
		}

		tc.setProjectId(projectId);
		tc.setName(testCaseName);
		initCaseIdIfExists(className, tc, testCaseName);

		testCasesToReload.add(tc);
	}

	private void initCaseIdIfExists(String className, TestCase tc, String testCaseName) {
		try {
			final Class<?> testClass = Class.forName(className);
			Method[] reflectedMethods = testClass.getMethods();
			for (Method reflectedMethod : reflectedMethods) {
				if (!testCaseName.endsWith("." + reflectedMethod.getName())) {
					continue;
				}
				if (reflectedMethod.isAnnotationPresent(net.nitrogen.ates.testpartner.ATESTest.class)) {
					tc.setMappingId(reflectedMethod.getAnnotation(net.nitrogen.ates.testpartner.ATESTest.class).mappingId());
				}
				else {
					tc.setMappingId(DEFAULT_TEST_CASE_ID);
				}
				break;
			}
		}
		catch (SecurityException e) {
			tc.setMappingId(DEFAULT_TEST_CASE_ID);
		}
		catch (ClassNotFoundException e) {
			tc.setMappingId(DEFAULT_TEST_CASE_ID);
		}
	}

}
