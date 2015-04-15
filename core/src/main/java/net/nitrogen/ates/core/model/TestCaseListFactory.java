package net.nitrogen.ates.core.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

public class TestCaseListFactory {
    private static TestCaseListFactory testCaseListFactory;

    private TestCaseListFactory() {
    }

    public static TestCaseListFactory me() {
        if (testCaseListFactory == null) {
            testCaseListFactory = new TestCaseListFactory();
        }
        return testCaseListFactory;
    }

    public List<TestCaseWithAdditionalInfo> createTestCaseListWithAdditionalInfoForProject(final long projectId) {
        return this.createTestCaseListWithAdditionalInfo("{CALL FindTestCasesForProjectWithAdditionalInfo(?)}", projectId);
    }

    public List<TestCaseWithAdditionalInfo> createTestCaseListWithAdditionalInfoForTestGroup(final long testGroupId) {
        return this.createTestCaseListWithAdditionalInfo("{CALL FindTestCasesForTestGroupWithAdditionalInfo(?)}", testGroupId);
    }

    @SuppressWarnings("unchecked")
    public List<TestCaseWithAdditionalInfo> createTestCaseListWithAdditionalInfoForTestSuite(final long testSuiteId) {
        return this.createTestCaseListWithAdditionalInfo("{CALL FindTestCasesForTestSuiteWithAdditionalInfo(?)}", testSuiteId);
    }

    @SuppressWarnings("unchecked")
    private List<TestCaseWithAdditionalInfo> createTestCaseListWithAdditionalInfo(final String prepareCall, final Long id) {
        return (List<TestCaseWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<TestCaseWithAdditionalInfo> testCasesWithAdditionalInfo = new ArrayList<>();

                try {
                    callSP = conn.prepareCall(prepareCall);
                    callSP.setLong(1, id);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            TestCaseWithAdditionalInfo testCaseWithAdditionalInfo = new TestCaseWithAdditionalInfo();
                            testCaseWithAdditionalInfo.setTestCase(TestCaseModel.createByResultSet(rs));
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(TestCaseWithAdditionalInfo.Fields.TEST_RESULT_ID));
                            result.setExecResult(rs.getInt(TestResultModel.Fields.EXEC_RESULT));
                            testCaseWithAdditionalInfo.setLatestTestResult(result);
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
}
