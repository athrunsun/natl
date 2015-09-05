package net.nitrogen.ates.core.model.test_suite;

public class TestSuiteWithAdditionalInfo {
    private TestSuiteModel testSuite;
    private int testCaseCount;

    public class Fields {
        public static final String TEST_CASE_COUNT = "test_case_count";
    }

    public TestSuiteModel getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuiteModel ts) {
        this.testSuite = ts;
    }

    public int getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }
}
