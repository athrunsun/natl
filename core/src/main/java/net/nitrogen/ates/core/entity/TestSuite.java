package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.TestSuiteModel;

public class TestSuite {
    private long id;
    private String name;
    private long projectId;

    public static TestSuite create(TestSuiteModel m) {
        TestSuite testSuite = new TestSuite();
        testSuite.setId(m.getLong(TestSuiteModel.Fields.ID));
        testSuite.setName(m.getStr(TestSuiteModel.Fields.NAME));
        testSuite.setProjectId(m.getLong(TestSuiteModel.Fields.PROJECT_ID));
        return testSuite;
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
}
