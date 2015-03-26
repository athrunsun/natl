package net.nitrogen.ates.core.model;

import java.util.List;

public class TestCaseWithAdditionalInfo {
    private TestCaseModel testCase;
    private TestResultModel latestTestResult;
    private List<TestResultModel> testResults;

    public TestCaseWithAdditionalInfo() {
    }

    public TestCaseWithAdditionalInfo(TestCaseModel testCase) {
        this.testCase = testCase;
        // Default test result number, 5
        this.testResults = TestResultModel.me.findTestResultsByCaseName(testCase.getName(), 5);
        this.latestTestResult = (this.testResults == null || this.testResults.size() < 1) ? null : testResults.get(0);
    }

    public TestResultModel getLatestTestResult() {
        return latestTestResult;
    }

    /**
     * @return the testCase
     */
    public TestCaseModel getTestCase() {
        return testCase;
    }

    /**
     * @return the testResults
     */
    public List<TestResultModel> getTestResults() {
        return testResults;
    }

    /**
     * @param testCase
     *            the testCase to set
     */
    public void setTestCase(TestCaseModel testCase) {
        this.testCase = testCase;
    }

    /**
     * @param latestTestResult
     *            the latestTestResult to set
     */
    public void setLatestTestResult(TestResultModel latestTestResult) {
        this.latestTestResult = latestTestResult;
    }

    /**
     * @param testResults
     *            the testResults to set
     */
    public void setTestResults(List<TestResultModel> testResults) {
        this.testResults = testResults;
    }
}
