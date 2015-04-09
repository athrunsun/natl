package net.nitrogen.ates.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;

import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.enumeration.ExecResult;

public class TestSuiteModel extends Model<TestSuiteModel> {
    public static final String TABLE = "test_suite";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final TestSuiteModel me = new TestSuiteModel();

    public long insert(long projectId, String suiteName) {
        TestSuiteModel newExecution = new TestSuiteModel();
        newExecution.set(Fields.NAME, suiteName).set(Fields.PROJECT_ID, projectId).save();
        return newExecution.get(Fields.ID);
    }

    public List<TestSuiteModel> findTestSuites(long projectId) {
        return this.find(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.PROJECT_ID),
                projectId);
    }

    public String getJvmOptionsAsString() {
        return CustomParameterModel.me.getJvmParametersAsString(CustomParameterDomainKey.TEST_SUITE, getId());
    }

    public Map<String, Integer> passrate(long testSuiteId) {
        List<TestCaseWithAdditionalInfo> testCaseWithAdditionalInfoList = TestCaseListFactory.me().createTestCaseListWithAdditionalInfoForTestSuite(testSuiteId);
        Map<String, Integer> execResultCount = new HashMap<>();

        int total = testCaseWithAdditionalInfoList.size();
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        int unknown = 0;

        for(TestCaseWithAdditionalInfo testCaseWithAdditionalInfo : testCaseWithAdditionalInfoList) {
            TestResultModel result = testCaseWithAdditionalInfo.getLatestTestResult();

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
        execResultCount.put(ExecutionModel.EXEC_RESULT_COUNT_MAP_KEY_TOTAL, total);

        return execResultCount;
    }

    public long getId() {
        return getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getName() {
        return getStr(Fields.NAME);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public long getProjectId() {
        return getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }
}
