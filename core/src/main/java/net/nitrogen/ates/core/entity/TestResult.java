package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.model.TestResultModel;
import net.nitrogen.ates.util.DateTimeUtil;
import net.nitrogen.ates.util.StringUtil;
import org.joda.time.DateTime;

public class TestResult {
    private long id;
    private long entryId;
    private String testName;
    private String slaveName;
    private DateTime startTime;
    private DateTime endTime;
    private int execResult;
    private String message;
    private String stackTrace;
    private String screenshotUrl;
    private long roundId;
    private long projectId;
    private String env;

    public static TestResult create(TestResultModel m) {
        TestResult testResult = new TestResult();
        testResult.setId(m.getLong(TestResultModel.Fields.ID));
        testResult.setEntryId(m.getLong(TestResultModel.Fields.ENTRY_ID));
        testResult.setTestName(m.getStr(TestResultModel.Fields.TEST_NAME));
        testResult.setSlaveName(m.getStr(TestResultModel.Fields.SLAVE_NAME));
        testResult.setStartTime(DateTimeUtil.fromSqlTimestamp(m.getTimestamp(TestResultModel.Fields.START_TIME)));
        testResult.setEndTime(DateTimeUtil.fromSqlTimestamp(m.getTimestamp(TestResultModel.Fields.END_TIME)));
        testResult.setExecResult(m.getInt(TestResultModel.Fields.EXEC_RESULT));
        testResult.setMessage(m.getStr(TestResultModel.Fields.MESSAGE));
        testResult.setStackTrace(m.getStr(TestResultModel.Fields.STACK_TRACE));
        testResult.setScreenshotUrl(m.getStr(TestResultModel.Fields.SCREENSHOT_URL));
        testResult.setRoundId(m.getLong(TestResultModel.Fields.ROUND_ID));
        testResult.setProjectId(m.getLong(TestResultModel.Fields.PROJECT_ID));
        testResult.setEnv(m.getStr(TestResultModel.Fields.ENV));
        return testResult;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public String getTestName() {
        return testName;
    }

    public String getShortTestName() {
        return StringUtil.shortenString(this.testName, TestCase.MAX_TEST_NAME_LENGTH);
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getSlaveName() {
        return slaveName;
    }

    public void setSlaveName(String slaveName) {
        this.slaveName = slaveName;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeAsString() {
        return this.startTime.toString(DateTimeUtil.DEFAULT_DATE_TIME_FORMAT);
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getEndTimeAsString() {
        return this.endTime.toString(DateTimeUtil.DEFAULT_DATE_TIME_FORMAT);
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public String getExecResultLabel() {
        return ExecResult.fromInt(execResult).toString();
    }

    public int getExecResult() {
        return execResult;
    }

    public void setExecResult(int execResult) {
        this.execResult = execResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    public long getRoundId() {
        return roundId;
    }

    public void setRoundId(long roundId) {
        this.roundId = roundId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
