package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;

import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestCaseWithAdditionalInfo;
import net.nitrogen.ates.core.model.TestCaseWithResult;
import net.nitrogen.ates.core.model.TestSuiteModel;
import net.nitrogen.ates.core.model.TestSuiteTestCaseModel;
import net.nitrogen.ates.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteController extends Controller {
    public void index() {
        setAttr("testsuiteList", TestSuiteModel.me.findTestSuites(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        long suiteId = getParaToLong(0);
        setAttr("testsuite", TestSuiteModel.me.findById(suiteId));
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr(
                "testCaseWithResultList",
                getResultList(ControllerHelper.getProjectPrefFromCookie(this), TestSuiteTestCaseModel.me.findTestSuiteTestCases(suiteId)));
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

    private List<TestCaseWithAdditionalInfo> getResultList(List<TestCaseModel> entries) {
        List<TestCaseWithAdditionalInfo> entriesWithResults = new ArrayList<>();

        for (TestCaseModel entry : entries) {
            entriesWithResults.add(new TestCaseWithAdditionalInfo(entry));
        }

        return entriesWithResults;
    }

    private List<TestCaseWithResult> getResultList(Long projectId, List<TestSuiteTestCaseModel> entries) {
        List<TestCaseWithResult> entriesWithResults = new ArrayList<>();

        for (TestSuiteTestCaseModel entry : entries) {
            entriesWithResults.add(new TestCaseWithResult(projectId, entry.getTestName()));
        }

        return entriesWithResults;
    }
}
