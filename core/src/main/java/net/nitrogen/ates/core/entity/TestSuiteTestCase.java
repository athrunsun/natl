package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.TestSuiteTestCaseModel;
import net.nitrogen.ates.util.StringUtil;

public class TestSuiteTestCase {
    private long id;
    private long testSuiteId;
    private String testName;

    public static TestSuiteTestCase create(TestSuiteTestCaseModel m) {
        TestSuiteTestCase ts_tc = new TestSuiteTestCase();
        ts_tc.setId(m.getLong(TestSuiteTestCaseModel.Fields.ID));
        ts_tc.setTestSuiteId(m.getLong(TestSuiteTestCaseModel.Fields.TEST_SUITE_ID));
        ts_tc.setTestName(m.getStr(TestSuiteTestCaseModel.Fields.TEST_NAME));
        return ts_tc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(long testSuiteId) {
        this.testSuiteId = testSuiteId;
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
