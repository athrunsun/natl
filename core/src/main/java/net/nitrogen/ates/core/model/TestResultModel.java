package net.nitrogen.ates.core.model;

import java.sql.Timestamp;
import java.util.List;

import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.util.DateTimeUtil;
import net.nitrogen.ates.util.StringUtil;

import org.joda.time.DateTime;

import com.jfinal.plugin.activerecord.Model;

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
        public static final String EXECUTION_ID = "execution_id";
        public static final String PROJECT_ID = "project_id";
        public static final String ENV = "env";
    }

    public static final TestResultModel me = new TestResultModel();

    public long getId() {
        return getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public long getEntryId() {
        return getLong(Fields.ENTRY_ID);
    }

    public void setEntryId(long entryId) {
        this.set(Fields.ENTRY_ID, entryId);
    }

    public String getTestName() {
        return getStr(Fields.TEST_NAME);
    }

    public String getShortTestName() {
        return StringUtil.shortenString(this.getTestName(), TestCaseModel.MAX_TEST_NAME_LENGTH, false);
    }

    public void setTestName(String testName) {
        this.set(Fields.TEST_NAME, testName);
    }

    public String getSlaveName() {
        return getStr(Fields.SLAVE_NAME);
    }

    public void setSlaveName(String slaveName) {
        this.set(Fields.SLAVE_NAME, slaveName);
    }

    public Timestamp getStartTimestamp() {
        return getTimestamp(TestResultModel.Fields.START_TIME);
    }

    public DateTime getStartTime() {
        return DateTimeUtil.fromSqlTimestamp(this.getStartTimestamp());
    }

    public String getStartTimeAsString() {
        return this.getStartTime().toString(DateTimeUtil.DEFAULT_DATE_TIME_FORMAT);
    }

    public void setStartTimestamp(Timestamp t) {
        set(Fields.START_TIME, t);
    }

    public void setStartTime(DateTime startTime) {
        this.setStartTimestamp(DateTimeUtil.toSqlTimestamp(startTime));
    }

    public Timestamp getEndTimestamp() {
        return getTimestamp(TestResultModel.Fields.END_TIME);
    }

    public DateTime getEndTime() {
        return DateTimeUtil.fromSqlTimestamp(this.getEndTimestamp());
    }

    public String getEndTimeAsString() {
        return this.getEndTime().toString(DateTimeUtil.DEFAULT_DATE_TIME_FORMAT);
    }

    public void setEndTimestamp(Timestamp t) {
        set(Fields.END_TIME, t);
    }

    public void setEndTime(DateTime endTime) {
        this.setEndTimestamp(DateTimeUtil.toSqlTimestamp(endTime));
    }

    public int getExecResult() {
        return getInt(Fields.EXEC_RESULT);
    }

    public String getExecResultLabel() {
        return ExecResult.fromInt(this.getExecResult()).toString();
    }

    public void setExecResult(int execResult) {
        set(Fields.EXEC_RESULT, execResult);
    }

    public void setExecResult(ExecResult execResult) {
        this.setExecResult(execResult.getValue());
    }

    public String getMessage() {
        return getStr(Fields.MESSAGE);
    }

    public void setMessage(String message) {
        this.set(Fields.MESSAGE, message);
    }

    public String getStackTrace() {
        return getStr(Fields.STACK_TRACE);
    }

    public void setStackTrace(String stackTrace) {
        this.set(Fields.STACK_TRACE, stackTrace);
    }

    public String getScreenshotUrl() {
        return getStr(Fields.SCREENSHOT_URL);
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.set(Fields.SCREENSHOT_URL, screenshotUrl);
    }

    public long getExecutionId() {
        return getLong(Fields.EXECUTION_ID);
    }

    public void setExecutionId(long executionId) {
        this.set(Fields.EXECUTION_ID, executionId);
    }

    public long getProjectId() {
        return getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }

    public String getEnv() {
        return getStr(Fields.ENV);
    }

    public void setEnv(String env) {
        this.set(Fields.ENV, env);
    }

    public TestResultModel findTestResult(long entryId) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? LIMIT 1",
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
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                TABLE,
                Fields.ENTRY_ID);

        return findFirst(sql, entryId);
    }

    public TestResultModel findTestResultByCaseName(String testName) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY '%s' DESC",
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
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                TABLE,
                Fields.TEST_NAME,
                Fields.ID);

        return findFirst(sql, testName);
    }

    public List<TestResultModel> findTestResults(long projectId) {
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
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                TABLE,
                Fields.PROJECT_ID,
                Fields.ID);

        return find(sql, projectId);
    }

    public long insertTestResult(TestResultModel testResult) {
        TestResultModel m = new TestResultModel();

        m.setEntryId(testResult.getEntryId());
        m.setTestName(testResult.getTestName());
        m.setSlaveName(testResult.getSlaveName());
        m.setStartTime(testResult.getStartTime());
        m.setEndTime(testResult.getEndTime());
        m.setExecResult(testResult.getExecResult());
        m.setMessage(testResult.getMessage());
        m.setStackTrace(testResult.getStackTrace());
        m.setScreenshotUrl(testResult.getScreenshotUrl());
        m.setExecutionId(testResult.getExecutionId());
        m.setProjectId(testResult.getProjectId());
        m.setEnv(testResult.getEnv());
        m.save();

        return m.getLong(Fields.ID);
    }
}
