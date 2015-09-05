package net.nitrogen.ates.core.model.test_suite;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import net.nitrogen.ates.core.model.test_group.TestGroupWithAdditionalInfo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestSuiteListFactory {
    private static TestSuiteListFactory testSuiteListFactory;

    private TestSuiteListFactory() {
    }

    public static TestSuiteListFactory me() {
        if (testSuiteListFactory == null) {
            testSuiteListFactory = new TestSuiteListFactory();
        }
        return testSuiteListFactory;
    }

    @SuppressWarnings("unchecked")
    public List<TestSuiteWithAdditionalInfo> createTestSuiteListWithAdditionalInfo(final long projectId) {
        return (List<TestSuiteWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<TestSuiteWithAdditionalInfo> testSuitesWithAdditionalInfo = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL FindTestSuitesWithAdditionalInfoForProject(?)}");
                    callSP.setLong(1, projectId);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            TestSuiteWithAdditionalInfo testSuiteWithAdditionalInfo = new TestSuiteWithAdditionalInfo();
                            testSuiteWithAdditionalInfo.setTestSuite(TestSuiteModel.createByResultSet(rs));
                            testSuiteWithAdditionalInfo.setTestCaseCount(rs.getInt(TestGroupWithAdditionalInfo.Fields.TEST_CASE_COUNT));
                            testSuitesWithAdditionalInfo.add(testSuiteWithAdditionalInfo);
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

                return testSuitesWithAdditionalInfo;
            }
        });
    }
}
