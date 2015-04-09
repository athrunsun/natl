package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.jfinal.aop.Before;
import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.CustomParameterType;
import net.nitrogen.ates.core.model.CustomParameterModel;
import net.nitrogen.ates.core.model.TestCaseListFactory;
import net.nitrogen.ates.core.model.TestGroupTestCaseModel;
import net.nitrogen.ates.core.model.TestSuiteModel;
import net.nitrogen.ates.core.model.TestSuiteTestCaseModel;
import net.nitrogen.ates.dashboard.interceptor.RawCustomParameterHandlingInterceptor;
import net.nitrogen.ates.util.StringUtil;

import com.jfinal.core.Controller;
import org.apache.commons.lang3.StringEscapeUtils;

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
        setAttr("customParameterList", CustomParameterModel.me.findParameters(CustomParameterDomainKey.TEST_SUITE, suiteId));
        render("detail.html");
    }

    public void create() {
        new TestSuiteModel().set(TestSuiteModel.Fields.NAME, getPara(TestSuiteModel.Fields.NAME))
                .set(TestSuiteModel.Fields.PROJECT_ID, ControllerHelper.getProjectPrefFromCookie(this)).save();
        redirect("/testsuite");
    }

    public void delete() {
        TestSuiteModel.me.deleteById(getParaToLong(0));
        redirect("/testsuite");
    }

    public void removeCaseFromSuite() {
        long suiteId = getParaToLong("testsuiteId");
        final String testcaseName = StringEscapeUtils.unescapeHtml4(getPara("testcaseName"));
        TestSuiteTestCaseModel.me.delete(suiteId, testcaseName);
        redirect(String.format("/testsuite/detail/%d", suiteId));
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

    @Before(RawCustomParameterHandlingInterceptor.class)
    public void updateCustomParameters() {
        Long testsuiteId = getParaToLong("testSuiteId");

        CustomParameterModel.me.overwriteTestSuiteParameters(ControllerHelper.getRawCustomParameterMap(this), testsuiteId);
        redirect(String.format("/testsuite/detail/%d", testsuiteId));
    }

    public void fetchJvmOptionsBySuiteIdAsJson() {
        renderJson(CustomParameterModel.me.findParameters(CustomParameterDomainKey.TEST_SUITE, getParaToLong("testSuiteId"), CustomParameterType.JVM));
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
