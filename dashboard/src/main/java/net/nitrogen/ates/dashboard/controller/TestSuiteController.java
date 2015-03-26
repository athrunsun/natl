package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.model.TestCaseListFactory;
import net.nitrogen.ates.core.model.TestSuiteModel;
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

    public void fetchTestSuitesByProjectIdAsJson() {
        renderJson(TestSuiteModel.me.findTestSuites(getParaToLong("projectId")));
    }

    public void assignTestCases() {
        Long testsuiteId = getParaToLong("testsuite");
        String suiteName = getPara("suitename");
        if (!StringUtil.isNullOrWhiteSpace(suiteName)) {
            // Create a suite and reassign the ID
        }
        String[] selectedTestCaseNames = getParaValues("selected_test_cases");
        redirect(String.format("/testsuite/detail/%d", testsuiteId));
    }
}
