package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.model.TestCaseModel;

import com.jfinal.core.Controller;

public class TestCaseController extends Controller {
    public void index() {
        this.setExecResultEnumAttr();
        setAttr("testCaseList", TestCaseModel.me.findTestCases(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    private void setExecResultEnumAttr() {
        setAttr("EXEC_RESULT_UNKNOWN", ExecResult.UNKNOWN.getValue());
        setAttr("EXEC_RESULT_SKIPPED", ExecResult.SKIPPED.getValue());
        setAttr("EXEC_RESULT_PASSED", ExecResult.PASSED.getValue());
        setAttr("EXEC_RESULT_FAILED", ExecResult.FAILED.getValue());
    }
}
