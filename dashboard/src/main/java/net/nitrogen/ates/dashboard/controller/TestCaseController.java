package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.model.test_case.TestCaseListFactory;

import com.jfinal.core.Controller;

public class TestCaseController extends Controller {
    public void index() {
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testCaseListWithAdditionalInfo", TestCaseListFactory.me()
                .createTestCaseListWithAdditionalInfoForProject(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }
}
