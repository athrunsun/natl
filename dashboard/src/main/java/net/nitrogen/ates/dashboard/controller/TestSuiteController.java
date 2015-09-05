package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.CustomParameterType;
import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.core.model.test_case.TestCaseListFactory;
import net.nitrogen.ates.core.model.test_group.TestGroupTestCaseModel;
import net.nitrogen.ates.core.model.test_suite.TestSuiteListFactory;
import net.nitrogen.ates.core.model.test_suite.TestSuiteModel;
import net.nitrogen.ates.core.model.test_suite.TestSuiteTestCaseModel;
import net.nitrogen.ates.dashboard.interceptor.RawCustomParameterHandlingInterceptor;
import net.nitrogen.ates.util.StringUtil;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

public class TestSuiteController extends Controller {
    public void index() {
        setAttr("testSuiteListWithAdditionalInfo", TestSuiteListFactory.me().createTestSuiteListWithAdditionalInfo(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        long suiteId = getParaToLong(0);
        final TestSuiteModel testSuite = TestSuiteModel.me.findById(suiteId);
        setAttr("testsuite", testSuite);
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testCaseListWithAdditionalInfo", TestCaseListFactory.me().createTestCaseListWithAdditionalInfoForTestSuite(testSuite.getId()));
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

    public void passrateAJAX() {
        long testSuiteId = getParaToLong("testSuiteId");
        renderJson(TestSuiteModel.me.passrate(testSuiteId));
    }

    public void removeCaseFromSuite() {
        long suiteId = getParaToLong("testsuiteId");
        final Long testCaseId = getParaToLong("testCaseId");
        TestSuiteTestCaseModel.me.delete(suiteId, testCaseId);
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
        String[] selectedTestCaseIds = getParaValues("selected_test_cases");
        List<TestSuiteTestCaseModel> testSuiteTestCases = new ArrayList<>(selectedTestCaseIds.length);
        for (String selectedTestCaseId : selectedTestCaseIds) {
            TestSuiteTestCaseModel testSuiteTestCaseModel = new TestSuiteTestCaseModel();
            testSuiteTestCaseModel.setTestSuiteId(testsuiteId);
            testSuiteTestCaseModel.setTestCaseId(Long.parseLong(selectedTestCaseId));
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
        HashSet<Long> caseIdsToBeAssigned = new HashSet();
        for (String selectedTestGroupId : selectedTestGroupIds) {
            List<TestGroupTestCaseModel> testcases = TestGroupTestCaseModel.me.findTestGroupTestCases(Long.parseLong(selectedTestGroupId));
            for (TestGroupTestCaseModel caseModel : testcases) {
                caseIdsToBeAssigned.add(caseModel.getTestCaseId());
            }
        }
        List<TestSuiteTestCaseModel> testSuiteTestCases = new ArrayList<TestSuiteTestCaseModel>(caseIdsToBeAssigned.size());
        for (long selectedTestCaseId : caseIdsToBeAssigned) {
            TestSuiteTestCaseModel testSuiteTestCaseModel = new TestSuiteTestCaseModel();
            testSuiteTestCaseModel.setTestSuiteId(testsuiteId);
            testSuiteTestCaseModel.setTestCaseId(selectedTestCaseId);
            testSuiteTestCases.add(testSuiteTestCaseModel);
        }

        TestSuiteTestCaseModel.me.insertTestSuiteTestCasesIfNotExists(testSuiteTestCases);
        redirect(String.format("/testsuite/detail/%d", testsuiteId));
    }
}
