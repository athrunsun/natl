package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import net.nitrogen.ates.core.enumeration.ExecResult;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueEntryWithResult {
    public class Fields {
        public static final String TEST_RESULT_ID = "test_result_id";
        public static final String TEST_RESULT_EXEC_RESULT = "test_result_exec_result";
    }

    private QueueEntryModel entryModel;
    private TestResultModel testResultModel;

    public static List<Map<String, Object>> createMapListForAllQueueEntries() {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (QueueEntryWithResult entryWithResult : createListForAllQueueEntries()) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    public static List<Map<String, Object>> createMapList(long executionId) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (QueueEntryWithResult entryWithResult : createListByExecution(executionId)) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    @SuppressWarnings("unchecked")
    private static List<QueueEntryWithResult> createListForAllQueueEntries() {
        return (List<QueueEntryWithResult>)Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryWithResult> entriesWithResult = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL GetAllQueueEntriesWithTestResult()}");
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            QueueEntryWithResult entryWithResult = new QueueEntryWithResult();
                            entryWithResult.entryModel = QueueEntryModel.createByResultSet(rs);
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(TestResultModel.Fields.ID));
                            result.setExecResult(rs.getInt(TestResultModel.Fields.EXEC_RESULT));
                            entryWithResult.testResultModel = result;
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
    private static List<QueueEntryWithResult> createListByExecution(final long executionId) {
        return (List<QueueEntryWithResult>)Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryWithResult> entriesWithResult = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL GetQueueEntriesWithTestResultByExecutionId(?)}");
                    callSP.setLong(1, executionId);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            QueueEntryWithResult entryWithResult = new QueueEntryWithResult();
                            entryWithResult.entryModel = QueueEntryModel.createByResultSet(rs);
                            TestResultModel result = new TestResultModel();
                            result.setId(rs.getLong(TestResultModel.Fields.ID));
                            result.setExecResult(rs.getInt(TestResultModel.Fields.EXEC_RESULT));
                            entryWithResult.testResultModel = result;
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

    private Map<String, Object> toMap() {
        Map<String, Object> fieldsMap = new HashMap<>();

        for (Map.Entry<String, Object> field : this.entryModel.getAttrsEntrySet()) {
            fieldsMap.put(field.getKey(), field.getValue());
        }

        if (this.testResultModel == null) {
            fieldsMap.put(Fields.TEST_RESULT_ID, null);
            fieldsMap.put(Fields.TEST_RESULT_EXEC_RESULT, ExecResult.UNKNOWN.getValue());
        } else {
            fieldsMap.put(Fields.TEST_RESULT_ID, this.testResultModel.getId());
            fieldsMap.put(Fields.TEST_RESULT_EXEC_RESULT, testResultModel.getExecResult());
        }

        return fieldsMap;
    }
}
