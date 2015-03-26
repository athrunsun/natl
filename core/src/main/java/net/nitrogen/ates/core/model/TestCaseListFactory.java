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
    private List<TestCaseWithAdditionalInfo> testcaseList;
    private static TestCaseListFactory testCaseListFactory;

    private TestCaseListFactory() {
    }

    public static TestCaseListFactory me() {
        if (testCaseListFactory == null) {
            testCaseListFactory = new TestCaseListFactory();
        }
        return testCaseListFactory;
    }

    @SuppressWarnings("unchecked")
    public List<TestCaseWithAdditionalInfo> createTestCaseListWithAdditionalInfo(final long projectId) {
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
                            testCaseWithAdditionalInfo.setTestCase(TestCaseModel.createByResultSet(rs));
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(TestResultModel.Fields.ID));
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

    @SuppressWarnings("unchecked")
    public List<TestCaseWithAdditionalInfo> createTestCaseListWithAdditionalInfo(final TestGroupModel testGroup) {
        return (List<TestCaseWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<TestCaseWithAdditionalInfo> testCasesWithAdditionalInfo = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL GetTestCasesForTestGroupWithAdditionalInfo(?)}");
                    callSP.setLong(1, testGroup.getId());
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            TestCaseWithAdditionalInfo testCaseWithAdditionalInfo = new TestCaseWithAdditionalInfo();
                            testCaseWithAdditionalInfo.setTestCase(TestCaseModel.createByResultSet(rs));
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(TestResultModel.Fields.ID));
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
