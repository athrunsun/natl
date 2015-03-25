package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestCaseWithAdditionalInfo {
    private TestCaseModel testCase;
    private TestResultModel latestTestResult;
    private List<TestResultModel> testResults;

    public TestCaseWithAdditionalInfo(){}

    public TestCaseWithAdditionalInfo(TestCaseModel testCase) {
        this.testCase = testCase;
        // Default test result number, 5
        this.testResults = TestResultModel.me.findTestResultsByCaseName(testCase.getName(), 5);
    }

    @SuppressWarnings("unchecked")
    public static List<TestCaseWithAdditionalInfo> createListForProject(final long projectId) {
        return (List<TestCaseWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<TestCaseWithAdditionalInfo> testCasesWithAdditionalInfo = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL GetTestCasesForProjectWithAdditionalInfo(?)}");
                    callSP.setLong(1, projectId);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            TestCaseWithAdditionalInfo testCaseWithAdditionalInfo = new TestCaseWithAdditionalInfo();
                            testCaseWithAdditionalInfo.testCase = TestCaseModel.createByResultSet(rs);
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(TestResultModel.Fields.ID));
                            result.setExecResult(rs.getInt(TestResultModel.Fields.EXEC_RESULT));
                            testCaseWithAdditionalInfo.latestTestResult = result;
                            testCasesWithAdditionalInfo.add(testCaseWithAdditionalInfo);
                        }

                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (callSP != null) {
                        callSP.close();
                    }
                }

                return testCasesWithAdditionalInfo;
            }
        });
    }

    public TestResultModel getLatestTestResult() {
        //return (this.testResults == null || this.testResults.size() < 1) ? null : testResults.get(0);
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
}
