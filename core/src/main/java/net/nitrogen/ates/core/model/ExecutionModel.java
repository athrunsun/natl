package net.nitrogen.ates.core.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;
import net.nitrogen.ates.util.DateTimeUtil;

import org.joda.time.DateTime;

import com.jfinal.plugin.activerecord.Model;

public class ExecutionModel extends Model<ExecutionModel> {
    public static final String EXEC_RESULT_COUNT_MAP_KEY_TOTAL = "TOTAL";
    public static final String DEFAULT_EXECUTION_NAME = "Unnamed";
    public static final String TABLE = "execution";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
        public static final String TEST_SUITE_ID = "test_suite_id";
        public static final String CREATED_TIME = "created_time";
    }

    public static final ExecutionModel me = new ExecutionModel();

    public static ExecutionModel createByResultSet(ResultSet rs) {
        ExecutionModel execution = new ExecutionModel();

        try {
            execution.setId(rs.getLong(Fields.ID));
            execution.setName(rs.getString(Fields.NAME));
            execution.setProjectId(rs.getLong(Fields.PROJECT_ID));
            execution.setCreatedTimestamp(rs.getTimestamp(Fields.CREATED_TIME));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return execution;
    }

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getName() {
        return this.getStr(Fields.NAME);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public long getProjectId() {
        return this.getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }

    public long getTestSuiteId() {
        return this.getLong(Fields.TEST_SUITE_ID);
    }

    public void setTestSuiteId(long testSuiteId) {
        this.set(Fields.TEST_SUITE_ID, testSuiteId);
    }

    public Timestamp getCreatedTimestamp() {
        return this.getTimestamp(Fields.CREATED_TIME);
    }

    public DateTime getCreatedTime() {
        Timestamp createdTimestamp = this.getCreatedTimestamp();
        return createdTimestamp == null ? null : DateTimeUtil.fromSqlTimestamp(createdTimestamp);
    }

    public String getCreatedTimeAsString() {
        return this.getCreatedTime() == null ? "" : DateTimeUtil.toStringWithDefaultFormat(this.getCreatedTime());
    }

    public void setCreatedTimestamp(Timestamp t) {
        this.set(Fields.CREATED_TIME, t);
    }

    public void setCreatedTime(DateTime createdTime) {
        this.setCreatedTimestamp(DateTimeUtil.toSqlTimestamp(createdTime));
    }

    public List<ExecutionModel> findExecutions(long projectId) {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC",
                Fields.ID,
                Fields.NAME,
                Fields.PROJECT_ID,
                TABLE,
                Fields.PROJECT_ID,
                Fields.ID), projectId);
    }

    public long createExecutionByTestCase(long projectId, String executionName, List<String> testCaseNames) {
        ExecutionModel newExecution = new ExecutionModel();
        newExecution.setName(executionName);
        newExecution.setProjectId(projectId);
        newExecution.setCreatedTime(DateTime.now());
        newExecution.save();
        long newExecutionId = newExecution.get(Fields.ID);
        List<QueueEntryModel> entries = new ArrayList<>();

        for (String testName : testCaseNames) {
            QueueEntryModel entry = new QueueEntryModel();
            entry.setStatus(QueueEntryStatus.WAITING.getStatus());
            entry.setName(testName);
            entry.setSlaveName("");
            entry.setExecutionId(newExecutionId);
            entry.setProjectId(projectId);
            entries.add(entry);
        }

        QueueEntryModel.me.insertEntries(entries);
        return newExecutionId;
    }

    public long createExecutionByTestGroup(long projectId, String executionName, List<Long> testGroupIds) {
        ExecutionModel newExecution = new ExecutionModel();
        newExecution.setName(executionName);
        newExecution.setProjectId(projectId);
        newExecution.setCreatedTime(DateTime.now());
        newExecution.save();
        long newExecutionId = newExecution.get(Fields.ID);
        Set<String> uniqueTestNames = new HashSet<String>();

        for (Long testGroupId : testGroupIds) {
            for (TestGroupTestCaseModel tg_tc : TestGroupTestCaseModel.me.findTestGroupTestCases(testGroupId.longValue())) {
                // uniqueTestNames.add(tg_tc.getTestCaseId());
                // TODO to be fixed.
            }
        }

        List<QueueEntryModel> entries = new ArrayList<QueueEntryModel>();

        for (String testName : uniqueTestNames) {
            QueueEntryModel entry = new QueueEntryModel();
            entry.setStatus(QueueEntryStatus.WAITING.getStatus());
            entry.setName(testName);
            entry.setSlaveName("");
            entry.setExecutionId(newExecutionId);
            entry.setProjectId(projectId);
            entries.add(entry);
        }

        QueueEntryModel.me.insertEntries(entries);
        return newExecutionId;
    }

    public long createExecutionByTestSuite(long projectId, String executionName, long testSuiteId) {
        ExecutionModel newExecution = new ExecutionModel();
        newExecution.setName(executionName);
        newExecution.setProjectId(projectId);
        newExecution.setCreatedTime(DateTime.now());
        newExecution.setTestSuiteId(testSuiteId);
        newExecution.save();
        long newExecutionId = newExecution.get(Fields.ID);
        Set<String> uniqueTestNames = new HashSet<String>();
        List<TestSuiteTestCaseModel> testCases = TestSuiteTestCaseModel.me.findTestSuiteTestCases(testSuiteId);
        CustomParameterModel.me.cloneParameters(CustomParameterDomainKey.TEST_SUITE, testSuiteId, newExecutionId);

        List<QueueEntryModel> entries = new ArrayList<QueueEntryModel>();

        for (TestSuiteTestCaseModel testCase : testCases) {
            QueueEntryModel entry = new QueueEntryModel();
            entry.setStatus(QueueEntryStatus.WAITING.getStatus());
            entry.setName(testCase.getTestCaseId());
            entry.setSlaveName("");
            entry.setExecutionId(newExecutionId);
            entry.setProjectId(projectId);
            entries.add(entry);
        }

        QueueEntryModel.me.insertEntries(entries);
        return newExecutionId;
    }

    public long cloneExecution(long executionId) {
        List<QueueEntryModel> existingEntries = QueueEntryModel.me.findEntries(executionId);

        if (existingEntries == null || existingEntries.size() <= 0) {
            return executionId;
        }

        ExecutionModel existingExecution = findFirst(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.ID),
                executionId);
        ExecutionModel newExecution = new ExecutionModel();
        newExecution.setName(String.format("%s_RerunALL", existingExecution.getName()));
        newExecution.setProjectId(existingExecution.getProjectId());
        newExecution.setCreatedTime(DateTime.now());
        newExecution.save();

        CustomParameterModel.me.cloneParameters(CustomParameterDomainKey.EXECUTION, existingExecution.getId(), newExecution.getId());

        List<QueueEntryModel> newEntries = new ArrayList<>();

        for (QueueEntryModel entry : existingEntries) {
            QueueEntryModel newEntry = new QueueEntryModel();
            newEntry.setStatus(QueueEntryStatus.WAITING.getStatus());
            newEntry.setName(entry.getName());
            newEntry.setSlaveName("");
            newEntry.setExecutionId(newExecution.getId());
            newEntry.setProjectId(existingExecution.getProjectId());
            newEntries.add(newEntry);
        }

        QueueEntryModel.me.insertEntries(newEntries);
        return newExecution.getId();
    }

    public long createExecutionByExecResult(long executionId, ExecResult execResult) {
        List<QueueEntryModel> existingEntries = QueueEntryModel.me.findEntries(executionId, execResult);

        if (existingEntries == null || existingEntries.size() <= 0) {
            return executionId;
        }

        ExecutionModel existingExecution = findFirst(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.ID),
                executionId);
        ExecutionModel newExecution = new ExecutionModel();
        newExecution.setName(String.format("%s_Rerun%s", existingExecution.getName(), execResult.toString()));
        newExecution.setProjectId(existingExecution.getProjectId());
        newExecution.setCreatedTime(DateTime.now());
        newExecution.save();

        CustomParameterModel.me.cloneParameters(CustomParameterDomainKey.EXECUTION, existingExecution.getId(), newExecution.getId());

        List<QueueEntryModel> newEntries = new ArrayList<>();

        for (QueueEntryModel entry : existingEntries) {
            QueueEntryModel newEntry = new QueueEntryModel();
            newEntry.setStatus(QueueEntryStatus.WAITING.getStatus());
            newEntry.setName(entry.getName());
            newEntry.setSlaveName("");
            newEntry.setExecutionId(newExecution.getId());
            newEntry.setProjectId(existingExecution.getProjectId());
            newEntries.add(newEntry);
        }

        QueueEntryModel.me.insertEntries(newEntries);
        return newExecution.getId();
    }

    public Map<String, Map<String, Integer>> passrateOfExecution(long executionId) {
        ExecutionModel execution = findFirst(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? LIMIT 1", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.ID),
                executionId);
        Map<String, Map<String, Integer>> passrates = new HashMap<>();
        passrates.put(String.format("%d,%s", execution.getId(), execution.getName()), this.execResultCount(execution.getId()));
        return passrates;
    }

    /**
     * 
     * @param projectId
     * @return A map, whose key is a string combination of execution id and name; and value is another map containing proportion of total queue entry count (in
     *         this execution) for each result status.
     */
    public Map<String, Map<String, Integer>> passrateOfRecentExecutions(long projectId, int count) {
        List<ExecutionModel> recentExecutions = find(String.format(
                "SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC LIMIT %d",
                Fields.ID,
                Fields.NAME,
                Fields.PROJECT_ID,
                TABLE,
                Fields.PROJECT_ID,
                Fields.ID,
                count), projectId);
        Map<String, Map<String, Integer>> passrates = new HashMap<>();

        for (ExecutionModel exec : recentExecutions) {
            passrates.put(String.format("%d,%s", exec.getId(), exec.getName()), this.execResultCount(exec.getId()));
        }

        return passrates;
    }

    private Map<String, Integer> execResultCount(long executionId) {
        Map<String, Integer> execResultCount = new HashMap<>();
        List<QueueEntryWithAdditionalInfo> entries = QueueEntryListFactory.createListForExecution(executionId);

        int total = entries.size();
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        int unknown = 0;

        for (QueueEntryWithAdditionalInfo queueEntryWithAdditionalInfo : entries) {
            TestResultModel result = queueEntryWithAdditionalInfo.getTestResultModel();

            if (result == null) {
                unknown += 1;
            } else {
                if (result.getExecResult() == ExecResult.PASSED.getValue()) {
                    passed += 1;
                } else if (result.getExecResult() == ExecResult.FAILED.getValue()) {
                    failed += 1;
                } else if (result.getExecResult() == ExecResult.SKIPPED.getValue()) {
                    skipped += 1;
                } else {
                    unknown += 1;
                }
            }
        }

        execResultCount.put(ExecResult.PASSED.toString(), passed);
        execResultCount.put(ExecResult.FAILED.toString(), failed);
        execResultCount.put(ExecResult.SKIPPED.toString(), skipped);
        execResultCount.put(ExecResult.UNKNOWN.toString(), unknown);
        execResultCount.put(EXEC_RESULT_COUNT_MAP_KEY_TOTAL, total);

        return execResultCount;
    }
}
