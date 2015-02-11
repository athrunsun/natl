package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.TestGroupTestCaseModel;
import net.nitrogen.ates.util.StringUtil;

public class TestGroupTestCase {
    private long id;
    private long testGroupId;
    private String testName;

    public static TestGroupTestCase create(TestGroupTestCaseModel m) {
        TestGroupTestCase tg_tc = new TestGroupTestCase();
        tg_tc.setId(m.getLong(TestGroupTestCaseModel.Fields.ID));
        tg_tc.setTestGroupId(m.getLong(TestGroupTestCaseModel.Fields.TEST_GROUP_ID));
        tg_tc.setTestName(m.getStr(TestGroupTestCaseModel.Fields.TEST_NAME));
        return tg_tc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestGroupId() {
        return testGroupId;
    }

    public void setTestGroupId(long testGroupId) {
        this.testGroupId = testGroupId;
    }

    public String getTestName() {
        return testName;
    }

    public String getShortTestName() {
        return StringUtil.shortenString(this.testName, TestCase.MAX_TEST_NAME_LENGTH);
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}
