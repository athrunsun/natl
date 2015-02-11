package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.util.StringUtil;

public class TestCase {
    public static final int MAX_TEST_NAME_LENGTH = 80;

    private long projectId;
    private String name;
    private String mappingId;

    public static TestCase create(TestCaseModel m) {
        TestCase testCase = new TestCase();
        testCase.setProjectId(m.getLong(TestCaseModel.Fields.PROJECT_ID));
        testCase.setName(m.getStr(TestCaseModel.Fields.NAME));
        testCase.setMappingId(m.getStr(TestCaseModel.Fields.MAPPING_ID));
        return testCase;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getMappingId() {
        return mappingId;
    }

    public void setMappingId(String mappingId) {
        this.mappingId = mappingId;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return StringUtil.shortenString(this.name, TestCase.MAX_TEST_NAME_LENGTH);
    }

    public void setName(String name) {
        this.name = name;
    }
}
