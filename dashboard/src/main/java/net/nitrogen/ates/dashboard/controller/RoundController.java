package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.entity.CustomEnv;
import net.nitrogen.ates.core.entity.Round;
import net.nitrogen.ates.core.model.CustomEnvModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.RoundModel;
import net.nitrogen.ates.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RoundController extends Controller {
    public void index() {
        setAttr("roundList", RoundModel.me.findRounds(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        long roundId = getParaToLong(0);
        setAttr("round", Round.create(RoundModel.me.findById(roundId)));
        render("detail.html");
    }

    public void createByTestGroup() {
        String roundName = getPara(RoundModel.Fields.NAME);
        roundName = StringUtil.isNullOrWhiteSpace(roundName) ? "" : roundName;
        String jvmOptions = getPara(QueueEntryModel.Fields.JVM_OPTIONS);
        jvmOptions = StringUtil.isNullOrWhiteSpace(jvmOptions) ? "" : jvmOptions;
        String testngParams = getPara(QueueEntryModel.Fields.PARAMS);
        testngParams = StringUtil.isNullOrWhiteSpace(testngParams) ? "" : testngParams;
        String envId = getPara(QueueEntryModel.Fields.ENV);
        String env = StringUtil.isNullOrWhiteSpace(envId) ? "" : CustomEnv.create(CustomEnvModel.me.findById(Long.parseLong(envId))).getName();
        String selectedTestGroups = getPara("selected_test_groups");
        List<Long> testGroupIds = new ArrayList<>();

        for(String testGroupIdAsString : selectedTestGroups.split(",")) {
            testGroupIds.add(Long.valueOf(testGroupIdAsString));
        }

        long roundId = RoundModel.me.createRoundByTestGroup(ControllerHelper.getProjectPrefFromCookie(this), roundName, env, jvmOptions, testngParams, testGroupIds);
        redirect(String.format("/round/detail/%d", roundId));
    }
}
