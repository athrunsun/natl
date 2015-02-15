package net.nitrogen.ates.testimporter2;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import net.nitrogen.ates.core.config.DBConfig;
import net.nitrogen.ates.core.entity.TestCase;
import net.nitrogen.ates.core.entity.TestGroup;
import net.nitrogen.ates.core.entity.TestGroupTestCase;
import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestGroupModel;
import net.nitrogen.ates.core.model.TestGroupTestCaseModel;
import net.nitrogen.ates.core.model.TestSuiteTestCaseModel;
import net.nitrogen.ates.util.PropertiesUtil;
import org.testng.ITestNGMethod;

import java.lang.reflect.Method;
import java.util.*;

public class TestImporter {
    private static final String TEST_CLASS_TEST_METHOD_DELIMITER = ".";
    private static final String DEFAULT_TEST_CASE_ID = "N/A";
    private static final long DEFAULT_PROJECT_ID = 1;
    private static final String projectIDProperty = "nitrogen_ates_projectid";

    private static List<TestCase> testCasesToReload = new ArrayList<TestCase>();
    private static List<TestGroup> testGroupsToReload = new ArrayList<TestGroup>();
    private static Map<String, List<String>> rawTestGroupTestCasesToReload = new HashMap<String, List<String>>();
    private long projectId;

    public void composeAndImport(List<ITestNGMethod> allTestMethods) {
        final String projectIDSaved = System.getProperty(projectIDProperty);

        if (projectIDSaved == null) {
            throw new RuntimeException(String.format("Project ID property is null!"));
        }

        this.projectId = Long.parseLong(projectIDSaved);

        readTestCaseAndGroups(projectId, allTestMethods);
        System.out.println(String.format("The number of test cases ready to import:%d", testCasesToReload.size()));
        System.out.println(String.format("The number of test groups ready to import:%d", testGroupsToReload.size()));
        doImport(testCasesToReload, testGroupsToReload, rawTestGroupTestCasesToReload);
    }

    private void readTestCaseAndGroups(long projectId, List<ITestNGMethod> allTestMethods) {
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
            } else {
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
                } else {
                    tc.setMappingId(DEFAULT_TEST_CASE_ID);
                }
                break;
            }
        } catch (SecurityException e) {
            tc.setMappingId(DEFAULT_TEST_CASE_ID);
        } catch (ClassNotFoundException e) {
            tc.setMappingId(DEFAULT_TEST_CASE_ID);
        }
    }

    private void doImport(List<TestCase> testCasesToReload, List<TestGroup> testGroupsToReload, Map<String, List<String>> rawTestGroupTestCasesToReload) {
        Properties props = PropertiesUtil.load("config.txt");
        DruidPlugin druidPlugin = DBConfig.createDruidPlugin(props.getProperty("jdbcUrl"), props.getProperty("dbuser"), props.getProperty("dbpassword"), 0, 0, 10);
        druidPlugin.start();
        ActiveRecordPlugin arp = DBConfig.createActiveRecordPlugin(druidPlugin);
        arp.start();

        TestCaseModel.me.reloadTestCases(this.projectId, testCasesToReload);
        TestGroupModel.me.deleteTestGroupsAndRespectiveTestGroupTestCases(this.projectId);
        TestGroupModel.me.insertTestGroups(testGroupsToReload);
        List<TestGroup> testGroups = TestGroupModel.me.findTestGroups(this.projectId);
        List<TestGroupTestCase> testGroupTestCases = new ArrayList<TestGroupTestCase>();

        for (Map.Entry<String, List<String>> rawTestGroupTestCase : rawTestGroupTestCasesToReload.entrySet()) {
            TestGroup targetTestGroup = this.searchTestGroup(testGroups, rawTestGroupTestCase.getKey());

            for (String testName : rawTestGroupTestCase.getValue()) {
                TestGroupTestCase tg_tc = new TestGroupTestCase();
                tg_tc.setTestGroupId(targetTestGroup.getId());
                tg_tc.setTestName(testName);
                testGroupTestCases.add(tg_tc);
            }
        }

        TestGroupTestCaseModel.me.insertTestGroupTestCases(testGroupTestCases);
        TestSuiteTestCaseModel.me.deleteNonexistent(this.projectId);

        arp.stop();
        druidPlugin.stop();
    }

    private TestGroup searchTestGroup(List<TestGroup> testGroups, String name) {
        for (TestGroup testGroup : testGroups) {
            if (testGroup.getName().equals(name)) {
                return testGroup;
            }
        }

        throw new RuntimeException(String.format("Test group not found by name:'%s'", name));
    }
}
