package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;

import java.util.*;

public class ExecutionModel extends Model<ExecutionModel> {
    public static final String TABLE = "execution";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final ExecutionModel me = new ExecutionModel();

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

    public List<ExecutionModel> findExecutions(long projectId) {
        return find(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.PROJECT_ID, Fields.ID),
                projectId);
    }

    public long createExecutionByTestGroup(long projectId, String executionName, String env, String jvmOptions, String params, List<Long> testGroupIds) {
        ExecutionModel newExecution = new ExecutionModel();
        newExecution.set(Fields.NAME, executionName).set(Fields.PROJECT_ID, projectId).save();
        long executionId = newExecution.get(Fields.ID);
        Set<String> uniqueTestNames = new HashSet<String>();

        for(Long testGroupId : testGroupIds) {
            for(TestGroupTestCaseModel tg_tc : TestGroupTestCaseModel.me.findTestGroupTestCases(testGroupId.longValue())) {
                uniqueTestNames.add(tg_tc.getTestName());
            }
        }

        List<QueueEntryModel> entries = new ArrayList<QueueEntryModel>();

        for(String testName : uniqueTestNames) {
            QueueEntryModel entry = new QueueEntryModel();
            entry.setStatus(QueueEntryStatus.WAITING.getStatus());
            entry.setName(testName);
            entry.setSlaveName("");
            entry.setExecutionId(executionId);
            entry.setProjectId(projectId);
            entry.setEnv(env);
            entry.setJvmOptions(jvmOptions);
            entry.setParams(params);
            entries.add(entry);
        }

        QueueEntryModel.me.insertEntries(entries);
        return executionId;
    }

    public Map<String, Map<String, Integer>> passrateOfExecution(long executionId) {
        ExecutionModel execution = findFirst(String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? LIMIT 1", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.ID), executionId);
        Map<String, Map<String, Integer>> passrates = new HashMap<>();
        Map<String, Integer> percentages = new HashMap<>();
        List<QueueEntryModel> entries = QueueEntryModel.me.findEntries(execution.getId());

        int total = entries.size();
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        int unknown = 0;

        for(QueueEntryModel entry : entries) {
            TestResultModel result = TestResultModel.me.findTestResult(entry.getId());

            if(result == null) {
                unknown += 1;
            }else {
                if(result.getExecResult() == ExecResult.PASSED.getValue()) {
                    passed += 1;
                }else if(result.getExecResult() == ExecResult.FAILED.getValue()) {
                    failed += 1;
                }else if(result.getExecResult() == ExecResult.SKIPPED.getValue()) {
                    skipped += 1;
                }else {
                    unknown += 1;
                }
            }
        }

        percentages.put(ExecResult.PASSED.toString(), passed);
        percentages.put(ExecResult.FAILED.toString(), failed);
        percentages.put(ExecResult.SKIPPED.toString(), skipped);
        percentages.put(ExecResult.UNKNOWN.toString(), unknown);
        percentages.put("TOTAL", total);

        passrates.put(String.format("%d,%s", execution.getId(), execution.getName()), percentages);
        return passrates;
    }

    /**
     *
     * @param projectId
     * @return A map, whose key is a string combination of execution id and name;
     * and value is another map containing proportion of total queue entry count (in this execution) for each result status.
     */
    public Map<String, Map<String, Integer>> passrateOfRecentExecutions(long projectId, int count) {
        List<ExecutionModel> lastFiveExecutions = find(String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC LIMIT %d", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.PROJECT_ID, Fields.ID, count), projectId);
        Map<String, Map<String, Integer>> passrates = new HashMap<>();

        for(ExecutionModel exec : lastFiveExecutions) {
            Map<String, Integer> percentages = new HashMap<>();
            List<QueueEntryModel> entries = QueueEntryModel.me.findEntries(exec.getId());

            int total = entries.size();
            int passed = 0;
            int failed = 0;
            int skipped = 0;
            int unknown = 0;

            for(QueueEntryModel entry : entries) {
                TestResultModel result = TestResultModel.me.findTestResult(entry.getId());

                if(result == null) {
                    unknown += 1;
                }else {
                    if(result.getExecResult() == ExecResult.PASSED.getValue()) {
                        passed += 1;
                    }else if(result.getExecResult() == ExecResult.FAILED.getValue()) {
                        failed += 1;
                    }else if(result.getExecResult() == ExecResult.SKIPPED.getValue()) {
                        skipped += 1;
                    }else {
                        unknown += 1;
                    }
                }
            }

            percentages.put(ExecResult.PASSED.toString(), passed);
            percentages.put(ExecResult.FAILED.toString(), failed);
            percentages.put(ExecResult.SKIPPED.toString(), skipped);
            percentages.put(ExecResult.UNKNOWN.toString(), unknown);
            percentages.put("TOTAL", total);

            passrates.put(String.format("%d,%s", exec.getId(), exec.getName()), percentages);
        }

        return passrates;
    }
}
