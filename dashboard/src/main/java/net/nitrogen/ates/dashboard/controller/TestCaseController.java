package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.TestCaseModel;

public class TestCaseController extends Controller {
    public void index() {
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testCaseList", TestCaseModel.me.findTestCases(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }
}
