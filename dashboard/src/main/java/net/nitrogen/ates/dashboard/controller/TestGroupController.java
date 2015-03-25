package net.nitrogen.ates.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;

import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestCaseWithResult;
import net.nitrogen.ates.core.model.TestGroupModel;
import net.nitrogen.ates.core.model.TestGroupTestCaseModel;

public class TestGroupController extends Controller {
    public void index() {
        setAttr("testGroupList", TestGroupModel.me.findTestGroups(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void detail() {
        final TestGroupModel testGroup = TestGroupModel.me.findTestGroup(getParaToLong(0));
        ControllerHelper.setExecResultEnumAttr(this);
        setAttr("testGroup", testGroup);
        setAttr(
                "testCaseWithResultList",
                getResultList(ControllerHelper.getProjectPrefFromCookie(this), TestGroupTestCaseModel.me.findTestGroupTestCases(testGroup.getId())));
        render("detail.html");
    }

    public void save() {
        long testGroupId = getParaToLong("test_group_id");
        TestGroupModel.me.findById(testGroupId).set(TestGroupModel.Fields.NAME, getPara("test_group_name")).update();
        redirect(String.format("/testgroup/detail/%d", testGroupId));
    }

    private List<TestCaseWithResult> getResultList(Long projectId, List<TestGroupTestCaseModel> list) {
        List<TestCaseWithResult> entriesWithResults = new ArrayList<>();

        for (TestGroupTestCaseModel entry : list) {
            entriesWithResults.add(new TestCaseWithResult(projectId, entry.getTestName()));
        }

        return entriesWithResults;
    }
}
