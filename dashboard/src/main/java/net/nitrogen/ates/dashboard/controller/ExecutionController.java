package net.nitrogen.ates.dashboard.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.core.model.email.EmailModel;
import net.nitrogen.ates.core.model.execution.ExecutionListFactory;
import net.nitrogen.ates.core.model.execution.ExecutionModel;
import net.nitrogen.ates.core.model.test_suite.TestSuiteModel;
import net.nitrogen.ates.dashboard.interceptor.RawCustomParameterHandlingInterceptor;
import net.nitrogen.ates.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ExecutionController extends Controller {
    public void index() {
        setAttr(
                "executionListWithAdditionalInfo",
                ExecutionListFactory.me().createExecutionListWithAdditionalInfo(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        long executionId = getParaToLong(0);
        setAttr("execution", ExecutionModel.me.findById(executionId));
        setAttr("customParameterList", CustomParameterModel.me.findParameters(CustomParameterDomainKey.EXECUTION, executionId));
        render("detail.html");
    }

    @Before(RawCustomParameterHandlingInterceptor.class)
    public void createByTestCase() {
        String executionName = getPara(ExecutionModel.Fields.NAME);
        List<Long> selectedTestCaseIds = new ArrayList<>();

        for (String testCaseId : getParaValues("selected_test_cases")) {
            selectedTestCaseIds.add(Long.parseLong(testCaseId));
        }

        executionName = StringUtil.isNullOrWhiteSpace(executionName) ? ExecutionModel.DEFAULT_EXECUTION_NAME : executionName;
        long newExecutionId = ExecutionModel.me.createExecutionByTestCase(ControllerHelper.getProjectPrefFromCookie(this), executionName, selectedTestCaseIds);
        CustomParameterModel.me.insertParameters(ControllerHelper.getRawCustomParameterMap(this), CustomParameterDomainKey.EXECUTION, newExecutionId);
        EmailModel.me.insertEmailForSending(newExecutionId);

        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

    @Before(RawCustomParameterHandlingInterceptor.class)
    public void createByTestGroup() {
        String executionName = getPara(ExecutionModel.Fields.NAME);
        String selectedTestGroups = getPara("selected_test_groups");
        executionName = StringUtil.isNullOrWhiteSpace(executionName) ? ExecutionModel.DEFAULT_EXECUTION_NAME : executionName;

        List<Long> testGroupIds = new ArrayList<>();
        for (String testGroupIdAsString : selectedTestGroups.split(",")) {
            testGroupIds.add(Long.valueOf(testGroupIdAsString));
        }

        long newExecutionId = ExecutionModel.me.createExecutionByTestGroup(ControllerHelper.getProjectPrefFromCookie(this), executionName, testGroupIds);

        CustomParameterModel.me.insertParameters(ControllerHelper.getRawCustomParameterMap(this), CustomParameterDomainKey.EXECUTION, newExecutionId);
        EmailModel.me.insertEmailForSending(newExecutionId);

        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

    public void createByTestSuite() {
        Long testSuiteId = getParaToLong(0);
        Long projectId = ControllerHelper.getProjectPrefFromCookie(this);

        long newExecutionId = ExecutionModel.me.createExecutionByTestSuite(
                projectId,
                String.format("CreatedFromSuite_%s", TestSuiteModel.me.findById(testSuiteId).getName()),
                testSuiteId);
        EmailModel.me.insertEmailForSending(newExecutionId);
        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

    public void fecthPassrateAsJson() {
        renderJson(ExecutionModel.me.passrateOfExecution(getParaToLong("executionId")));
    }

    public void rerunAll() {
        final long newExecutionId = ExecutionModel.me.cloneExecution(getParaToLong(0));
        EmailModel.me.insertEmailForSending(newExecutionId);
        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

    public void rerunFailed() {
        final long newExecutionId = ExecutionModel.me.createExecutionByExecResult(getParaToLong(0), ExecResult.FAILED);
        EmailModel.me.insertEmailForSending(newExecutionId);
        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

    public void rerunUnsuccessful() {
        final long newExecutionId = ExecutionModel.me.createExecutionByExecResult(getParaToLong(0));
        EmailModel.me.insertEmailForSending(newExecutionId);
        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

}
