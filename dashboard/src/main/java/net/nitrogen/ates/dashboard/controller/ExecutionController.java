package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.CustomEnvModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.ExecutionModel;
import net.nitrogen.ates.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ExecutionController extends Controller {
    public void index() {
        setAttr("executionList", ExecutionModel.me.findExecutions(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        long executionId = getParaToLong(0);
        setAttr("execution", ExecutionModel.me.findById(executionId));
        render("detail.html");
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

        for(String testGroupIdAsString : selectedTestGroups.split(",")) {
            testGroupIds.add(Long.valueOf(testGroupIdAsString));
        }

        long executionId = ExecutionModel.me.createExecutionByTestGroup(ControllerHelper.getProjectPrefFromCookie(this), executionName, env, jvmOptions, testngParams, testGroupIds);
        redirect(String.format("/execution/detail/%d", executionId));
    }

    public void fecthPassrateAsJson() {
        renderJson(ExecutionModel.me.passrateOfExecution(getParaToLong("executionId")));
    }
}
