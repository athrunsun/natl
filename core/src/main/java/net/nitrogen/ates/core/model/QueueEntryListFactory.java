package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueueEntryListFactory {
    private static QueueEntryListFactory queueEntryListFactory;

    private QueueEntryListFactory() {
    }

    public QueueEntryListFactory me() {
        if (queueEntryListFactory == null) {
            queueEntryListFactory = new QueueEntryListFactory();
        }

        return queueEntryListFactory;
    }

    public static List<Map<String, Object>> createMapListForAllQueueEntries(int pageNumber) {
        return createMapListForAllQueueEntries(pageNumber, QueueEntryModel.DEFAULT_PAGE_SIZE);
    }

    public static List<Map<String, Object>> createMapListForAllQueueEntries(int pageNumber, int pageSize) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (QueueEntryWithAdditionalInfo entryWithResult : createListForAllQueueEntries(pageNumber, pageSize)) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    public static List<Map<String, Object>> createMapList(long executionId, int pageNumber) {
        return createMapList(executionId, pageNumber, QueueEntryModel.DEFAULT_PAGE_SIZE);
    }

    public static List<Map<String, Object>> createMapList(long executionId, int pageNumber, int pageSize) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (QueueEntryWithAdditionalInfo entryWithResult : createListByExecution(executionId, pageNumber, pageSize)) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    @SuppressWarnings("unchecked")
    private static List<QueueEntryWithAdditionalInfo> createListForAllQueueEntries(final int pageNumber, final int pageSize) {
        return (List<QueueEntryWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryWithAdditionalInfo> entriesWithResult = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL GetAllQueueEntriesWithAdditionalInfo(?,?)}");
                    callSP.setInt(1, pageNumber);
                    callSP.setInt(2, pageSize);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            QueueEntryWithAdditionalInfo entryWithResult = new QueueEntryWithAdditionalInfo();
                            entryWithResult.setEntryModel(QueueEntryModel.createByResultSet(rs));
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(QueueEntryWithAdditionalInfo.Fields.TEST_RESULT_ID));
                            result.setExecResult(rs.getInt(TestResultModel.Fields.EXEC_RESULT));
                            entryWithResult.setTestResultModel(result);
                            entriesWithResult.add(entryWithResult);
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

                return entriesWithResult;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static List<QueueEntryWithAdditionalInfo> createListByExecution(final long executionId, final int pageNumber, final int pageSize) {
        return (List<QueueEntryWithAdditionalInfo>)Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryWithAdditionalInfo> entriesWithResult = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL GetQueueEntriesWithAdditionalInfoByExecutionId(?,?,?)}");
                    callSP.setLong(1, executionId);
                    callSP.setInt(2, pageNumber);
                    callSP.setInt(3, pageSize);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            QueueEntryWithAdditionalInfo entryWithResult = new QueueEntryWithAdditionalInfo();
                            entryWithResult.setEntryModel(QueueEntryModel.createByResultSet(rs));
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(QueueEntryWithAdditionalInfo.Fields.TEST_RESULT_ID));
                            result.setExecResult(rs.getInt(TestResultModel.Fields.EXEC_RESULT));
                            entryWithResult.setTestResultModel(result);
                            entriesWithResult.add(entryWithResult);
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

                return entriesWithResult;
            }
        });
    }
}
