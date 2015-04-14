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

    public static List<Map<String, Object>> createMapListForAllQueueEntriesWithPaging(int pageNumber) {
        return createMapListForAllQueueEntriesWithPaging(pageNumber, QueueEntryModel.DEFAULT_PAGE_SIZE);
    }

    public static List<Map<String, Object>> createMapListForAllQueueEntriesWithPaging(int pageNumber, int pageSize) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (QueueEntryWithAdditionalInfo entryWithResult : createListForAllQueueEntriesWithPaging(pageNumber, pageSize)) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    public static List<Map<String, Object>> createMapListWithPaging(long executionId, int pageNumber) {
        return createMapListWithPaging(executionId, pageNumber, QueueEntryModel.DEFAULT_PAGE_SIZE);
    }

    public static List<Map<String, Object>> createMapListWithPaging(long executionId, int pageNumber, int pageSize) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (QueueEntryWithAdditionalInfo entryWithResult : createListForExecutionWithPaging(executionId, pageNumber, pageSize)) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    @SuppressWarnings("unchecked")
    public static List<QueueEntryWithAdditionalInfo> createListForExecution(final long executionId) {
        return (List<QueueEntryWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryWithAdditionalInfo> entriesWithResult = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL FindQueueEntriesWithAdditionalInfoForExecution(?)}");
                    callSP.setLong(1, executionId);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            QueueEntryWithAdditionalInfo entryWithResult = new QueueEntryWithAdditionalInfo();
                            entryWithResult.setEntryModel(QueueEntryModel.createByResultSet(rs));

                            TestCaseModel testCaseModel = TestCaseModel.me.findValidTestCase(
                                    entryWithResult.getEntryModel().getProjectId(),
                                    rs.getLong(QueueEntryWithAdditionalInfo.Fields.TEST_CASE_NAME));

                            entryWithResult.setTestCaseModel(testCaseModel);

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
    private static List<QueueEntryWithAdditionalInfo> createListForAllQueueEntriesWithPaging(final int pageNumber, final int pageSize) {
        return (List<QueueEntryWithAdditionalInfo>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryWithAdditionalInfo> entriesWithResult = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL FindAllQueueEntriesWithAdditionalInfo_Paging(?,?)}");
                    callSP.setInt(1, pageNumber);
                    callSP.setInt(2, pageSize);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            QueueEntryWithAdditionalInfo entryWithResult = new QueueEntryWithAdditionalInfo();
                            entryWithResult.setEntryModel(QueueEntryModel.createByResultSet(rs));

                            TestCaseModel testCaseModel = TestCaseModel.me.findValidTestCase(
                                    entryWithResult.getEntryModel().getProjectId(),
                                    rs.getLong(QueueEntryWithAdditionalInfo.Fields.TEST_CASE_NAME));

                            entryWithResult.setTestCaseModel(testCaseModel);

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
    private static List<QueueEntryWithAdditionalInfo> createListForExecutionWithPaging(final long executionId, final int pageNumber, final int pageSize) {
        return (List<QueueEntryWithAdditionalInfo>)Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryWithAdditionalInfo> entriesWithResult = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL FindQueueEntriesWithAdditionalInfoForExecution_Paging(?,?,?)}");
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

                            TestCaseModel testCaseModel = TestCaseModel.me.findValidTestCase(
                                    entryWithResult.getEntryModel().getProjectId(),
                                    rs.getLong(QueueEntryWithAdditionalInfo.Fields.TEST_CASE_NAME));

                            entryWithResult.setTestCaseModel(testCaseModel);

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
