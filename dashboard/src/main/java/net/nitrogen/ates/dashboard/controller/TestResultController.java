package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.model.ExecutionModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.TestResultModel;

import com.jfinal.core.Controller;

public class TestResultController extends Controller {
    public void detail() {
        ControllerHelper.setExecResultEnumAttr(this);
        TestResultModel testResult = TestResultModel.me.findById(getParaToLong(0));
        setAttr("testResult", testResult);
        setAttr("execution", ExecutionModel.me.findById(testResult.getExecutionId()));
        setAttr("entry", QueueEntryModel.me.findById(testResult.getEntryId()));
        render("detail.html");
    }
}
