package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.ExecutionModel;
import net.nitrogen.ates.core.model.TestResultModel;

public class TestResultController extends Controller {
    public void index() {
        this.setExecResultEnumAttr();
        setAttr("testResultList", TestResultModel.me.findTestResults(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        this.setExecResultEnumAttr();
        TestResultModel testResult = TestResultModel.me.findById(getParaToLong(0));
        setAttr("testResult", testResult);
        setAttr("round", ExecutionModel.me.findById(testResult.getExecutionId()));
        setAttr("entry", QueueEntryModel.me.findById(testResult.getEntryId()));
        render("detail.html");
    }

    private void setExecResultEnumAttr() {
        setAttr("EXEC_RESULT_UNKNOWN", ExecResult.UNKNOWN.getValue());
        setAttr("EXEC_RESULT_SKIPPED", ExecResult.SKIPPED.getValue());
        setAttr("EXEC_RESULT_PASSED", ExecResult.PASSED.getValue());
        setAttr("EXEC_RESULT_FAILED", ExecResult.FAILED.getValue());
    }
}
