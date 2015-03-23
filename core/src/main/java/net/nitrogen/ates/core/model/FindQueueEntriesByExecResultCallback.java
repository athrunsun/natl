package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.ICallback;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.util.DateTimeUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FindQueueEntriesByExecResultCallback implements ICallback {
    private long executionId;
    private ExecResult execResult;

    public FindQueueEntriesByExecResultCallback(long execId, ExecResult result) {
        this.executionId = execId;
        this.execResult = result;
    }

    @Override
    public Object call(Connection conn) throws SQLException {
        CallableStatement callSP = null;
        List<QueueEntryModel> entries = new ArrayList<>();

        try {
            callSP = conn.prepareCall("{CALL GetQueueEntriesByExecutionIdAndExecResult(?,?)}");
            callSP.setLong(1, executionId);
            callSP.setInt(2, execResult.getValue());
            boolean hadResults = callSP.execute();

            if (hadResults) {
                ResultSet rs = callSP.getResultSet();
                rs.beforeFirst();

                while (rs.next()) {
                    QueueEntryModel entry = new QueueEntryModel();
                    entry.setId(rs.getLong(QueueEntryModel.Fields.ID));
                    entry.setStatus(rs.getInt(QueueEntryModel.Fields.STATUS));
                    entry.setName(rs.getString(QueueEntryModel.Fields.NAME));
                    entry.setSlaveName(rs.getString(QueueEntryModel.Fields.SLAVE_NAME));
                    entry.setIndex(rs.getInt(QueueEntryModel.Fields.INDEX));
                    entry.setStartTimestamp(rs.getTimestamp(QueueEntryModel.Fields.START_TIME));
                    entry.setEndTimestamp(rs.getTimestamp(QueueEntryModel.Fields.END_TIME));
                    entry.setExecutionId(rs.getLong(QueueEntryModel.Fields.EXECUTION_ID));
                    entry.setProjectId(rs.getLong(QueueEntryModel.Fields.PROJECT_ID));
                    entry.setEnv(rs.getString(QueueEntryModel.Fields.ENV));
                    entry.setJvmOptions(rs.getString(QueueEntryModel.Fields.JVM_OPTIONS));
                    entry.setParams(rs.getString(QueueEntryModel.Fields.PARAMS));
                    entries.add(entry);
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

        return entries;
    }
}