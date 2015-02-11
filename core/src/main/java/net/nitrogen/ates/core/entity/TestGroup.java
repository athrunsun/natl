package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.TestGroupModel;
import net.nitrogen.ates.core.model.TestGroupTestCaseModel;

import java.util.List;

public class TestGroup {
    private long id;
    private String name;
    private long projectId;
    private List<TestGroupTestCase> testGroupTestCases;

    public static TestGroup create(TestGroupModel m) {
        TestGroup testGroup = new TestGroup();
        long testGroupId = m.getLong(TestGroupModel.Fields.ID);
        testGroup.setId(testGroupId);
        testGroup.setName(m.getStr(TestGroupModel.Fields.NAME));
        testGroup.setProjectId(m.getLong(TestGroupModel.Fields.PROJECT_ID));
        testGroup.setTestGroupTestCases(TestGroupTestCaseModel.me.findTestGroupTestCases(testGroupId));
        return testGroup;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public List<TestGroupTestCase> getTestGroupTestCases() {
        return testGroupTestCases;
    }

    public void setTestGroupTestCases(List<TestGroupTestCase> testGroupTestCases) {
        this.testGroupTestCases = testGroupTestCases;
    }
}
