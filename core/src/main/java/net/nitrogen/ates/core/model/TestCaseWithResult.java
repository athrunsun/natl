package net.nitrogen.ates.core.model;

import net.nitrogen.ates.core.enumeration.ExecResult;
import java.util.List;

public class TestCaseWithResult {
    private TestCaseModel testcase;
    private List<TestResultModel> testResults;

    public TestCaseWithResult(TestCaseModel testcase) {
        this.testcase = testcase;
        // Default test result number, 5
        this.testResults = TestResultModel.me.findTestResultsByCaseName(testcase.getName(), 5);
    }

    public TestResultModel getLatestTestResult() {
        return (this.testResults == null || this.testResults.size() < 1) ? null : testResults.get(0);
    }

    /**
     * @return the testcase
     */
    public TestCaseModel getTestcase() {
        return testcase;
    }

    /**
     * @return the testResults
     */
    public List<TestResultModel> getTestResults() {
        return testResults;
    }
}
