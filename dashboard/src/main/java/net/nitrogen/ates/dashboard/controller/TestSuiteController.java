package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import net.nitrogen.ates.core.model.TestCaseListFactory;
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
        if (!StringUtil.isNullOrWhiteSpace(suiteName)) {
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
}
