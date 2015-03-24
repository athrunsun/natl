package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;

import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.model.CustomEnvModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.ExecutionModel;
import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestCaseWithResult;
import net.nitrogen.ates.core.model.TestSuiteModel;
import net.nitrogen.ates.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
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
        setAttr("testCaseWithResultList", getResultList(TestCaseModel.me.findTestCases(ControllerHelper.getProjectPrefFromCookie(this))));
        render("detail.html");
    }

    private List<TestCaseWithResult> getResultList(List<TestCaseModel> entries) {
        List<TestCaseWithResult> entriesWithResults = new ArrayList<>();

        for (TestCaseModel entry : entries) {
            entriesWithResults.add(new TestCaseWithResult(entry));
        }

        return entriesWithResults;
    }
}
