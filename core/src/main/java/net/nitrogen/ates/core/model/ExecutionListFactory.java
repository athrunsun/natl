package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecutionListFactory {
    private static ExecutionListFactory executionListFactory;

    private ExecutionListFactory() {
    }

    public static ExecutionListFactory me() {
        if (executionListFactory == null) {
            executionListFactory = new ExecutionListFactory();
        }
        return executionListFactory;
    }

    @SuppressWarnings("unchecked")
    public List<ExecutionWithAdditionalInfo> createExecutionListWithAdditionalInfo(final long projectId) {
        return (List<ExecutionWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<ExecutionWithAdditionalInfo> executionsWithAdditionalInfo = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL GetExecutionsWithAdditionalInfo(?)}");
                    callSP.setLong(1, projectId);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            ExecutionWithAdditionalInfo executionWithAdditionalInfo = new ExecutionWithAdditionalInfo();
                            executionWithAdditionalInfo.setExecution(ExecutionModel.createByResultSet(rs));
                            executionWithAdditionalInfo.setQueueEntryCount(rs.getInt(ExecutionWithAdditionalInfo.Fields.QUEUE_ENTRY_COUNT));
                            executionsWithAdditionalInfo.add(executionWithAdditionalInfo);
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

                return executionsWithAdditionalInfo;
            }
        });
    }
}
