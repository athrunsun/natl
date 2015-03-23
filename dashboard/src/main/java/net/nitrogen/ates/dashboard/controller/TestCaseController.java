package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestCaseWithResult;

import com.jfinal.core.Controller;

public class TestCaseController extends Controller {
    public void index() {
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testCaseWithResultList", getResultList(TestCaseModel.me.findTestCases(ControllerHelper.getProjectPrefFromCookie(this))));
        render("index.html");
    }

    private List<TestCaseWithResult> getResultList(List<TestCaseModel> entries) {
        List<TestCaseWithResult> entriesWithResults = new ArrayList<>();

        for (TestCaseModel entry : entries) {
            entriesWithResults.add(new TestCaseWithResult(entry));
        }

        return entriesWithResults;
    }
}
