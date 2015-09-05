package net.nitrogen.ates.core.model.test_group;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestGroupListFactory {
    private static TestGroupListFactory testGroupListFactory;

    private TestGroupListFactory() {
    }

    public static TestGroupListFactory me() {
        if (testGroupListFactory == null) {
            testGroupListFactory = new TestGroupListFactory();
        }
        return testGroupListFactory;
    }

    @SuppressWarnings("unchecked")
    public List<TestGroupWithAdditionalInfo> createTestGroupListWithAdditionalInfo(final long projectId) {
        return (List<TestGroupWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<TestGroupWithAdditionalInfo> testGroupsWithAdditionalInfo = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL FindTestGroupsWithAdditionalInfoForProject(?)}");
                    callSP.setLong(1, projectId);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            TestGroupWithAdditionalInfo testGroupWithAdditionalInfo = new TestGroupWithAdditionalInfo();
                            testGroupWithAdditionalInfo.setTestGroup(TestGroupModel.createByResultSet(rs));
                            testGroupWithAdditionalInfo.setTestCaseCount(rs.getInt(TestGroupWithAdditionalInfo.Fields.TEST_CASE_COUNT));
                            testGroupsWithAdditionalInfo.add(testGroupWithAdditionalInfo);
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

                return testGroupsWithAdditionalInfo;
            }
        });
    }
}
