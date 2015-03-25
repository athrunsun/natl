package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestCaseWithAdditionalInfo;

import com.jfinal.core.Controller;

public class TestCaseController extends Controller {
    public void index() {
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testCaseListWithAdditionalInfo", TestCaseWithAdditionalInfo.createListForProject(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    private List<TestCaseWithAdditionalInfo> getResultList(List<TestCaseModel> entries) {
        List<TestCaseWithAdditionalInfo> entriesWithResults = new ArrayList<>();

        for (TestCaseModel entry : entries) {
            entriesWithResults.add(new TestCaseWithAdditionalInfo(entry));
        }

        return entriesWithResults;
    }
}
