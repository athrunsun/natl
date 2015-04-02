package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.model.CustomEnvModel;
import net.nitrogen.ates.core.model.CustomParameterModel;
import net.nitrogen.ates.core.model.ExecutionListFactory;
import net.nitrogen.ates.core.model.ExecutionModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.util.StringUtil;

import com.jfinal.core.Controller;

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
        render("detail.html");
    }

    public void createByTestCase() {
        String executionName = getPara(ExecutionModel.Fields.NAME);
        String[] selectedTestCaseNames = getParaValues("selected_test_cases");
        String[] customFieldName = getParaValues("customFieldName");
        String[] customFieldValue = getParaValues("customFieldValue");
        String[] customFieldType = getParaValues("customFieldType");
        executionName = StringUtil.isNullOrWhiteSpace(executionName) ? "" : executionName;

        // ***** to be deleted
        String jvmOptions = getPara(QueueEntryModel.Fields.JVM_OPTIONS);
        jvmOptions = StringUtil.isNullOrWhiteSpace(jvmOptions) ? "" : jvmOptions;
        String testngParams = getPara(QueueEntryModel.Fields.PARAMS);
        testngParams = StringUtil.isNullOrWhiteSpace(testngParams) ? "" : testngParams;
        String envId = getPara(QueueEntryModel.Fields.ENV);
        String env = StringUtil.isNullOrWhiteSpace(envId) ? "" : CustomEnvModel.me.findById(Long.parseLong(envId)).getName();
        // ***** end

        long newExecutionId = ExecutionModel.me.createExecutionByTestCase(
                ControllerHelper.getProjectPrefFromCookie(this),
                executionName,
                env,
                jvmOptions,
                testngParams,
                Arrays.asList(selectedTestCaseNames));
        CustomParameterModel.me.insertExecutionParameters(customFieldName, customFieldValue, newExecutionId, customFieldType);
        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

    public void createByTestGroup() {
        String executionName = getPara(ExecutionModel.Fields.NAME);
        executionName = StringUtil.isNullOrWhiteSpace(executionName) ? "" : executionName;
        String jvmOptions = getPara(QueueEntryModel.Fields.JVM_OPTIONS);
        jvmOptions = StringUtil.isNullOrWhiteSpace(jvmOptions) ? "" : jvmOptions;
        String testngParams = getPara(QueueEntryModel.Fields.PARAMS);
        testngParams = StringUtil.isNullOrWhiteSpace(testngParams) ? "" : testngParams;
        String envId = getPara(QueueEntryModel.Fields.ENV);
        String env = StringUtil.isNullOrWhiteSpace(envId) ? "" : CustomEnvModel.me.findById(Long.parseLong(envId)).getName();
        String selectedTestGroups = getPara("selected_test_groups");
        List<Long> testGroupIds = new ArrayList<>();

        for (String testGroupIdAsString : selectedTestGroups.split(",")) {
            testGroupIds.add(Long.valueOf(testGroupIdAsString));
        }

        long newExecutionId = ExecutionModel.me.createExecutionByTestGroup(
                ControllerHelper.getProjectPrefFromCookie(this),
                executionName,
                env,
                jvmOptions,
                testngParams,
                testGroupIds);
        redirect(String.format("/execution/detail/%d", newExecutionId));
    }

    public void fecthPassrateAsJson() {
        renderJson(ExecutionModel.me.passrateOfExecution(getParaToLong("executionId")));
    }

    public void rerunAll() {
        redirect(String.format("/execution/detail/%d", ExecutionModel.me.cloneExecution(getParaToLong(0))));
    }

    public void rerunFailed() {
        redirect(String.format("/execution/detail/%d", ExecutionModel.me.createExecutionByExecResult(getParaToLong(0), ExecResult.FAILED)));
    }
}
