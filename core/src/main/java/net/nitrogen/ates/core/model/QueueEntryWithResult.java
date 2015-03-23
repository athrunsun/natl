package net.nitrogen.ates.core.model;

import net.nitrogen.ates.core.enumeration.ExecResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueEntryWithResult {
    private QueueEntryModel entryModel;
    private TestResultModel testResultModel;

    public static QueueEntryWithResult create(QueueEntryModel entry) {
        QueueEntryWithResult entryWithResult = new QueueEntryWithResult();
        entryWithResult.entryModel = entry;
        entryWithResult.testResultModel = TestResultModel.me.findTestResult(entry.getId());
        return entryWithResult;
    }

    public static List<QueueEntryWithResult> createList(List<QueueEntryModel> entries) {
        List<QueueEntryWithResult> entriesWithResult = new ArrayList<>();

        for (QueueEntryModel entry : entries) {
            entriesWithResult.add(QueueEntryWithResult.create(entry));
        }

        return entriesWithResult;
    }

    public static List<Map<String, Object>> createMapList(List<QueueEntryModel> entries) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (QueueEntryWithResult entryWithResult : QueueEntryWithResult.createList(entries)) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsMap = new HashMap<>();

        for (Map.Entry<String, Object> field : this.entryModel.getAttrsEntrySet()) {
            fieldsMap.put(field.getKey(), field.getValue());
        }

        if (this.testResultModel == null) {
            fieldsMap.put(TestResultModel.Fields.EXEC_RESULT, ExecResult.UNKNOWN.getValue());
        } else {
            fieldsMap.put(TestResultModel.Fields.EXEC_RESULT, testResultModel.getExecResult());
        }

        return fieldsMap;
    }
}
