package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.TestResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestResultModel extends Model<TestResultModel> {
    public static final String TABLE = "test_result";

    public class Fields {
        public static final String ID = "id";
        public static final String ENTRY_ID = "entry_id";
        public static final String TEST_NAME = "test_name";
        public static final String SLAVE_NAME = "slave_name";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String EXEC_RESULT = "exec_result";
        public static final String MESSAGE = "message";
        public static final String STACK_TRACE = "stack_trace";
        public static final String SCREENSHOT_URL = "screenshot_url";
        public static final String ROUND_ID = "round_id";
        public static final String PROJECT_ID = "project_id";
        public static final String ENV = "env";
    }

    public static final TestResultModel me = new TestResultModel();

    public List<TestResult> findTestResults(long projectId) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC",
                Fields.ID,
                Fields.ENTRY_ID,
                Fields.TEST_NAME,
                Fields.SLAVE_NAME,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.EXEC_RESULT,
                Fields.MESSAGE,
                Fields.STACK_TRACE,
                Fields.SCREENSHOT_URL,
                Fields.ROUND_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                TABLE,
                Fields.PROJECT_ID,
                Fields.ID);

        List<TestResult> testResults = new ArrayList<>();

        for(TestResultModel m : find(sql, projectId)) {
            testResults.add(TestResult.create(m));
        }

        return testResults;
    }

    public long insertTestResult(final TestResult testResult) {
//        Db.execute(new ICallback() {
//            @Override
//            public Object call(Connection conn) {
//                try {
//                    CallableStatement statement = conn.prepareCall("{CALL InsertTestResult(?,?,?,?,?,?,?,?,?,?,?,?)}");
//                    statement.setLong(1, testResult.getEntryId());
//                    statement.setString(2, testResult.getTestName());
//                    statement.setString(3, testResult.getSlaveName());
//                    statement.setString(4, testResult.getStartTimeAsString());
//                    statement.setString(5, testResult.getEndTimeAsString());
//                    statement.setInt(6, testResult.getExecResult());
//                    statement.setString(7, testResult.getMessage());
//                    statement.setString(8, testResult.getStackTrace());
//                    statement.setString(9, testResult.getScreenshotUrl());
//                    statement.setLong(10, testResult.getRoundId());
//                    statement.setLong(11, testResult.getProjectId());
//                    statement.setString(12, testResult.getEnv());
//                    statement.execute();
//                    ResultSet rs = statement.getResultSet();
//                    ResultSetMetaData md = rs.getMetaData();
//                    int columns = md.getColumnCount();
//                    rs.close();
//                    return null;
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        });

        TestResultModel m = new TestResultModel();

        m.set(Fields.ENTRY_ID, testResult.getEntryId()).
                set(Fields.TEST_NAME, testResult.getTestName()).
                set(Fields.SLAVE_NAME, testResult.getSlaveName()).
                set(Fields.START_TIME, testResult.getStartTimeAsString()).
                set(Fields.END_TIME, testResult.getEndTimeAsString()).
                set(Fields.EXEC_RESULT, testResult.getExecResult()).
                set(Fields.MESSAGE, testResult.getMessage()).
                set(Fields.STACK_TRACE, testResult.getStackTrace()).
                set(Fields.SCREENSHOT_URL, testResult.getScreenshotUrl()).
                set(Fields.ROUND_ID, testResult.getRoundId()).
                set(Fields.PROJECT_ID, testResult.getProjectId()).
                set(Fields.ENV, testResult.getEnv()).save();

        return m.getLong(Fields.ID);
    }
}
