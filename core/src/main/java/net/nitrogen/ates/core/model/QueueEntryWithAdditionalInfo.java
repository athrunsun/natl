package net.nitrogen.ates.core.model;

import net.nitrogen.ates.core.enumeration.ExecResult;

import java.util.HashMap;
import java.util.Map;

public class QueueEntryWithAdditionalInfo {
    public class Fields {
        public static final String TEST_CASE_NAME = "test_case_name";
        public static final String TEST_RESULT_ID = "test_result_id";
    }

    private QueueEntryModel entryModel;
    private TestCaseModel testCaseModel;
    private TestResultModel testResultModel;

    public QueueEntryModel getEntryModel() {
        return entryModel;
    }

    public void setEntryModel(QueueEntryModel entryModel) {
        this.entryModel = entryModel;
    }

    public TestCaseModel getTestCaseModel() {
        return testCaseModel;
    }

    public void setTestCaseModel(TestCaseModel testCaseModel) {
        this.testCaseModel = testCaseModel;
    }

    public TestResultModel getTestResultModel() {
        return testResultModel;
    }

    public void setTestResultModel(TestResultModel testResultModel) {
        this.testResultModel = testResultModel;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsMap = new HashMap<>();

        for (Map.Entry<String, Object> field : this.entryModel.getAttrsEntrySet()) {
            fieldsMap.put(field.getKey(), field.getValue());
        }

        fieldsMap.put(Fields.TEST_CASE_NAME, getTestCaseModel().getName());

        if (this.testResultModel == null) {
            fieldsMap.put(Fields.TEST_RESULT_ID, null);
            fieldsMap.put(TestResultModel.Fields.EXEC_RESULT, ExecResult.UNKNOWN.getValue());
        } else {
            fieldsMap.put(Fields.TEST_RESULT_ID, this.testResultModel.getId());
            fieldsMap.put(TestResultModel.Fields.EXEC_RESULT, testResultModel.getExecResult());
        }

        return fieldsMap;
    }
}
