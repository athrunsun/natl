package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.model.TestCaseListFactory;
import net.nitrogen.ates.core.model.TestGroupModel;

import com.jfinal.core.Controller;

public class TestGroupController extends Controller {
    public void index() {
        setAttr("testGroupList", TestGroupModel.me.findTestGroups(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        final TestGroupModel testGroup = TestGroupModel.me.findTestGroup(getParaToLong(0));
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testGroup", testGroup);
        setAttr("testCaseListWithAdditionalInfo", TestCaseListFactory.me().createTestCaseListWithAdditionalInfo(testGroup));
        render("detail.html");
    }

    public void save() {
        long testGroupId = getParaToLong("test_group_id");
        TestGroupModel.me.findById(testGroupId).set(TestGroupModel.Fields.NAME, getPara("test_group_name")).update();
        redirect(String.format("/testgroup/detail/%d", testGroupId));
    }

}
