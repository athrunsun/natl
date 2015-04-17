package net.nitrogen.ates.core.model.test_case;

import net.nitrogen.ates.core.model.test_result.TestResultModel;

public class TestCaseWithAdditionalInfo {
    private TestCaseModel testCase;
    private TestResultModel latestTestResult;

    public class Fields {
        public static final String TEST_RESULT_ID = "test_result_id";
    }

    public TestCaseModel getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCaseModel testCase) {
        this.testCase = testCase;
    }

    public TestResultModel getLatestTestResult() {
        return latestTestResult;
    }

    public void setLatestTestResult(TestResultModel latestTestResult) {
        this.latestTestResult = latestTestResult;
    }
}
