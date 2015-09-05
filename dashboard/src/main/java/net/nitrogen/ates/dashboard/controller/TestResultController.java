package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.model.execution.ExecutionModel;
import net.nitrogen.ates.core.model.queue_entry.QueueEntryModel;
import net.nitrogen.ates.core.model.test_case.TestCaseModel;
import net.nitrogen.ates.core.model.test_result.TestResultModel;

import com.jfinal.core.Controller;

public class TestResultController extends Controller {
    public void detail() {
        ControllerHelper.setExecResultEnumAttr(this);
        TestResultModel testResult = TestResultModel.me.findById(getParaToLong(0));
        setAttr("testResult", testResult);
        setAttr("testCase", TestCaseModel.me.findById(testResult.getTestCaseId()));
        setAttr("execution", ExecutionModel.me.findById(testResult.getExecutionId()));
        setAttr("entry", QueueEntryModel.me.findById(testResult.getEntryId()));
        render("detail.html");
    }
}
