package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.ICallback;

import java.sql.*;

public class FetchQueueEntryCallback implements ICallback {
    private final String slaveName;

    public FetchQueueEntryCallback(String slave) {
        this.slaveName = slave;
    }

    @Override
    public Object call(Connection conn) throws SQLException {
        PreparedStatement beginLock = null;
        PreparedStatement releaseLock = null;
        CallableStatement callSP = null;
        //QueueEntry entry = null;
        long entryId = 0;

        try {
            conn.setAutoCommit(false);
            beginLock = conn.prepareStatement("LOCK TABLES `slave` READ,`queue_entry` WRITE;");
            releaseLock = conn.prepareStatement("UNLOCK TABLES;");

            beginLock.execute();

            callSP = conn.prepareCall("{CALL AssignQueueEntry(?,?)}");
            callSP.setString(1, slaveName);
            callSP.registerOutParameter(2, Types.INTEGER);
            callSP.execute();
            callSP.getLong(2);

            if(!callSP.wasNull()) {
                entryId = callSP.getLong(2);
            }

//            boolean hadResults = callSP.execute();
//
//            if(hadResults) {
//                ResultSet rs = callSP.getResultSet();
//                ResultSetMetaData md = rs.getMetaData();
//                int columns = md.getColumnCount();
//                rs.beforeFirst();
//
//                if (rs.next()) {
//                    entry = new QueueEntry();
//                    entry.setId(rs.getLong(QueueEntryModel.Fields.ID));
//                    entry.setStatus(rs.getInt(QueueEntryModel.Fields.STATUS));
//                    entry.setName(rs.getString(QueueEntryModel.Fields.NAME));
//                    entry.setSlaveName(rs.getString(QueueEntryModel.Fields.SLAVE_NAME));
//                    entry.setIndex(rs.getInt(QueueEntryModel.Fields.INDEX));
//                    entry.setStartTime(DateTimeUtil.fromSqlTimestamp(rs.getTimestamp(QueueEntryModel.Fields.START_TIME)));
//                    entry.setEndTime(DateTimeUtil.fromSqlTimestamp(rs.getTimestamp(QueueEntryModel.Fields.END_TIME)));
//                    entry.setExecutionId(rs.getLong(QueueEntryModel.Fields.EXECUTION_ID));
//                    entry.setProjectId(rs.getLong(QueueEntryModel.Fields.PROJECT_ID));
//                    entry.setEnv(rs.getString(QueueEntryModel.Fields.ENV));
//                    entry.setJvmOptions(rs.getString(QueueEntryModel.Fields.JVM_OPTIONS));
//                    entry.setParams(rs.getString(QueueEntryModel.Fields.PARAMS));
//                }
//
//                rs.close();
//            }

            conn.commit();
            releaseLock.execute();
        } catch (SQLException e) {
            e.printStackTrace();

            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            if (beginLock != null) {
                beginLock.close();
            }

            if (releaseLock != null) {
                releaseLock.close();
            }

            if (callSP != null) {
                callSP.close();
            }

            conn.setAutoCommit(true);
            //return entry;
        }

        return Long.valueOf(entryId);
    }
}
