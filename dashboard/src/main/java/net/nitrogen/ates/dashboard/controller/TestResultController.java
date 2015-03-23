package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.ExecutionModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.TestResultModel;

public class TestResultController extends Controller {
    public void index() {
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testResultList", TestResultModel.me.findTestResults(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        ControllerHelper.setExecResultEnumAttr(this);
        TestResultModel testResult = TestResultModel.me.findById(getParaToLong(0));
        setAttr("testResult", testResult);
        setAttr("execution", ExecutionModel.me.findById(testResult.getExecutionId()));
        setAttr("entry", QueueEntryModel.me.findById(testResult.getEntryId()));
        render("detail.html");
    }
}
