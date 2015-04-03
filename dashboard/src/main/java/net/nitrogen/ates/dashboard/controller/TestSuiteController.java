package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.nitrogen.ates.core.model.CustomParameterModel;
import net.nitrogen.ates.core.model.TestCaseListFactory;
import net.nitrogen.ates.core.model.TestGroupTestCaseModel;
import net.nitrogen.ates.core.model.TestSuiteModel;
import net.nitrogen.ates.core.model.TestSuiteTestCaseModel;
import net.nitrogen.ates.util.StringUtil;

import com.jfinal.core.Controller;

public class TestSuiteController extends Controller {
    public void index() {
        setAttr("testsuiteList", TestSuiteModel.me.findTestSuites(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        long suiteId = getParaToLong(0);
        final TestSuiteModel testSuite = TestSuiteModel.me.findById(suiteId);
        setAttr("testsuite", testSuite);
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testCaseListWithAdditionalInfo", TestCaseListFactory.me().createTestCaseListWithAdditionalInfo(testSuite));
        render("detail.html");
    }

    public void create() {
        new TestSuiteModel().set(TestSuiteModel.Fields.NAME, getPara(TestSuiteModel.Fields.NAME))
                .set(TestSuiteModel.Fields.PROJECT_ID, ControllerHelper.getProjectPrefFromCookie(this)).save();
        redirect("/testsuite");
    }

    public void delete() {
        renderText(String.valueOf(TestSuiteModel.me.deleteById(getParaToLong("testsuiteId"))));
    }

    public void removeCaseFromSuite() {
        long suiteId = getParaToLong("testsuiteId");
        final String testcaseName = getPara("testcaseName");
        renderText(String.valueOf(TestSuiteTestCaseModel.me.delete(suiteId, testcaseName)));
    }

    public void fetchTestSuitesByProjectIdAsJson() {
        renderJson(TestSuiteModel.me.findTestSuites(getParaToLong("projectId")));
    }

    public void assignTestCases() {
        Long testsuiteId = getParaToLong("testsuite");
        String suiteName = getPara("suitename");
        if (!StringUtil.isNullOrWhiteSpace(suiteName) || testsuiteId == null || testsuiteId < 1) {
            // Create the suite
            testsuiteId = TestSuiteModel.me.insert(ControllerHelper.getProjectPrefFromCookie(this), suiteName);
        }

        // Construct a list for assignment
        String[] selectedTestCaseNames = getParaValues("selected_test_cases");
        List<TestSuiteTestCaseModel> testSuiteTestCases = new ArrayList<TestSuiteTestCaseModel>(selectedTestCaseNames.length);
        for (String selectedTestCaseName : selectedTestCaseNames) {
            TestSuiteTestCaseModel testSuiteTestCaseModel = new TestSuiteTestCaseModel();
            testSuiteTestCaseModel.setTestSuiteId(testsuiteId);
            testSuiteTestCaseModel.setTestName(selectedTestCaseName);
            testSuiteTestCases.add(testSuiteTestCaseModel);
        }

        TestSuiteTestCaseModel.me.insertTestSuiteTestCasesIfNotExists(testSuiteTestCases);
        redirect(String.format("/testsuite/detail/%d", testsuiteId));
    }

    public void updateJvmOptions() {
        Long testsuiteId = getParaToLong("testSuiteId");
        String[] keys = getParaValues("customFieldName");
        String[] values = getParaValues("customFieldValue");
        String[] types = new String[] { "1", "0" }; // default to 0 as JVM

        CustomParameterModel.me.overwriteTestSuiteParameters(keys, values, testsuiteId, types);
        redirect("/testsuite/index");
    }

    public void fetchJvmOptionsBySuiteIdAsJson() {
        renderJson(CustomParameterModel.me.findTestSuiteParameters(getParaToLong("testSuiteId"), 0));
    }

    public void fetchJvmOptions() {
        Long testsuiteId = getParaToLong("testSuiteId");
        String[] keys = getParaValues("customFieldName");
        String[] values = getParaValues("customFieldValue");
        String[] types = new String[] { "1", "0" }; // default to 0 as JVM

        CustomParameterModel.me.overwriteTestSuiteParameters(keys, values, testsuiteId, types);
        redirect("/testsuite/index");
    }

    public void assignTestGroups() {
        Long testsuiteId = getParaToLong("testsuite");
        String suiteName = getPara("suitename");
        if (!StringUtil.isNullOrWhiteSpace(suiteName) || testsuiteId == null || testsuiteId < 1) {
            // Create the suite
            testsuiteId = TestSuiteModel.me.insert(ControllerHelper.getProjectPrefFromCookie(this), suiteName);
        }

        // Construct a list for assignment
        String[] selectedTestGroupIds = getParaValues("selected_test_groups");
        HashSet<String> caseNamesToBeAssigned = new HashSet();
        for (String selectedTestGroupId : selectedTestGroupIds) {
            List<TestGroupTestCaseModel> testcases = TestGroupTestCaseModel.me.findTestGroupTestCases(Long.parseLong(selectedTestGroupId));
            for (TestGroupTestCaseModel caseModel : testcases) {
                caseNamesToBeAssigned.add(caseModel.getTestName());
            }
        }
        List<TestSuiteTestCaseModel> testSuiteTestCases = new ArrayList<TestSuiteTestCaseModel>(caseNamesToBeAssigned.size());
        for (String selectedTestCaseName : caseNamesToBeAssigned) {
            TestSuiteTestCaseModel testSuiteTestCaseModel = new TestSuiteTestCaseModel();
            testSuiteTestCaseModel.setTestSuiteId(testsuiteId);
            testSuiteTestCaseModel.setTestName(selectedTestCaseName);
            testSuiteTestCases.add(testSuiteTestCaseModel);
        }

        TestSuiteTestCaseModel.me.insertTestSuiteTestCasesIfNotExists(testSuiteTestCases);
        redirect(String.format("/testsuite/detail/%d", testsuiteId));
    }
}
