package net.nitrogen.ates.testimporter2;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import net.nitrogen.ates.core.config.DBConfig;
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

    private static List<TestCaseModel> testCasesToReload = new ArrayList<TestCaseModel>();
    private static List<TestGroupModel> testGroupsToReload = new ArrayList<TestGroupModel>();
    private static Map<String, List<String>> rawTestGroupTestCasesToReload = new HashMap<String, List<String>>();
    private long projectId;

    public void composeAndImport(List<ITestNGMethod> allTestMethods) {
        Properties props = PropertiesUtil.load("config.txt");
        DruidPlugin druidPlugin = DBConfig.createDruidPlugin(props.getProperty("jdbcUrl"), props.getProperty("dbuser"), props.getProperty("dbpassword"), 0, 0, 10);
        druidPlugin.start();
        ActiveRecordPlugin arp = DBConfig.createActiveRecordPlugin(druidPlugin);
        arp.start();

        final String projectIDSaved = System.getProperty(projectIDProperty);

        if (projectIDSaved == null) {
            throw new RuntimeException(String.format("Project ID property is null!"));
        }

        this.projectId = Long.parseLong(projectIDSaved);

        readTestCaseAndGroups(projectId, allTestMethods);
        System.out.println(String.format("The number of test cases ready to import:%d", testCasesToReload.size()));
        System.out.println(String.format("The number of test groups ready to import:%d", testGroupsToReload.size()));
        doImport(testCasesToReload, testGroupsToReload, rawTestGroupTestCasesToReload);

        arp.stop();
        druidPlugin.stop();
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

            for (TestGroupModel g : testGroupsToReload) {
                if (g.getName().equals(group)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                TestGroupModel testGroup = new TestGroupModel();
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
        TestCaseModel tc = new TestCaseModel();
        String className = method.toString().replaceAll(".*instance:", "").replaceAll("@.*", "");
        String testCaseName = String.format("%s%s%s", className, TEST_CLASS_TEST_METHOD_DELIMITER, method.getMethodName());

        for (TestCaseModel testcase : testCasesToReload) {
            if (testcase.getName().equals(testCaseName)) {
                return;
            }
        }

        tc.setProjectId(projectId);
        tc.setName(testCaseName);
        initCaseIdIfExists(className, tc, testCaseName);

        testCasesToReload.add(tc);
    }

    private void initCaseIdIfExists(String className, TestCaseModel tc, String testCaseName) {
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

    private void doImport(List<TestCaseModel> testCasesToReload, List<TestGroupModel> testGroupsToReload, Map<String, List<String>> rawTestGroupTestCasesToReload) {
        TestCaseModel.me.reloadTestCases(this.projectId, testCasesToReload);
        TestGroupModel.me.deleteTestGroupsAndRespectiveTestGroupTestCases(this.projectId);
        TestGroupModel.me.insertTestGroups(testGroupsToReload);
        List<TestGroupModel> testGroups = TestGroupModel.me.findTestGroups(this.projectId);
        List<TestGroupTestCaseModel> testGroupTestCases = new ArrayList<TestGroupTestCaseModel>();

        for (Map.Entry<String, List<String>> rawTestGroupTestCase : rawTestGroupTestCasesToReload.entrySet()) {
            TestGroupModel targetTestGroup = this.searchTestGroup(testGroups, rawTestGroupTestCase.getKey());

            for (String testName : rawTestGroupTestCase.getValue()) {
                TestGroupTestCaseModel tg_tc = new TestGroupTestCaseModel();
                tg_tc.setTestGroupId(targetTestGroup.getId());
                tg_tc.setTestName(testName);
                testGroupTestCases.add(tg_tc);
            }
        }

        TestGroupTestCaseModel.me.insertTestGroupTestCases(testGroupTestCases);
        TestSuiteTestCaseModel.me.deleteNonexistent(this.projectId);
    }

    private TestGroupModel searchTestGroup(List<TestGroupModel> testGroups, String name) {
        for (TestGroupModel testGroup : testGroups) {
            if (testGroup.getName().equals(name)) {
                return testGroup;
            }
        }

        throw new RuntimeException(String.format("Test group not found by name:'%s'", name));
    }
}
