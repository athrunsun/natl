package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.model.TestCaseListFactory;

import com.jfinal.core.Controller;

public class TestCaseController extends Controller {
    public void index() {
        ControllerHelper.setExecResultEnumAttr(this);
        System.out.println(getPara("name"));
        setAttr("testCaseListWithAdditionalInfo", TestCaseListFactory.me()
                .createTestCaseListWithAdditionalInfo(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }
}
