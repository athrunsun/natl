package net.nitrogen.ates.core.model;

import net.nitrogen.ates.core.enumeration.ExecResult;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class QueueEntryWithResult {
//    private long id;
//    private int status;
//    private String name;
//    private String slaveName;
//    private int index;
//    private DateTime startTime;
//    private DateTime endTime;
//    private long executionId;
//    private long projectId;
//    private String jvmOptions;
//    private String params;
//    private int execResult;
//
//    public static QueueEntryWithResult create(QueueEntryModel entry) {
//        QueueEntryWithResult entryWithResult = new QueueEntryWithResult();
//        entryWithResult.setId(entry.getId());
//        entryWithResult.setStatus(entry.getStatus());
//        entryWithResult.setName(entry.getName());
//        entryWithResult.setSlaveName(entry.getSlaveName());
//        entryWithResult.setIndex(entry.getIndex());
//        entryWithResult.setStartTime(entry.getStartTime());
//        entryWithResult.setEndTime(entry.getEndTime());
//        entryWithResult.setExecutionId(entry.getExecutionId());
//        entryWithResult.setProjectId(entry.getProjectId());
//        entryWithResult.setJvmOptions(entry.getJvmOptions());
//        entryWithResult.setParams(entry.getParams());
//
//        TestResultModel testResult = TestResultModel.me.findTestResult(entry.getId());
//
//        if (testResult == null) {
//            entryWithResult.setExecResult(ExecResult.UNKNOWN.getValue());
//        } else {
//            entryWithResult.setExecResult(testResult.getExecResult());
//        }
//
//        return entryWithResult;
//    }
//
//    public static List<QueueEntryWithResult> createList(List<QueueEntryModel> entries) {
//        List<QueueEntryWithResult> entriesWithResult = new ArrayList<>();
//
//        for(QueueEntryModel entry : entries) {
//            entriesWithResult.add(QueueEntryWithResult.create(entry));
//        }
//
//        return entriesWithResult;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getSlaveName() {
//        return slaveName;
//    }
//
//    public void setSlaveName(String slaveName) {
//        this.slaveName = slaveName;
//    }
//
//    public int getIndex() {
//        return index;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }
//
//    public DateTime getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(DateTime startTime) {
//        this.startTime = startTime;
//    }
//
//    public DateTime getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(DateTime endTime) {
//        this.endTime = endTime;
//    }
//
//    public long getExecutionId() {
//        return executionId;
//    }
//
//    public void setExecutionId(long executionId) {
//        this.executionId = executionId;
//    }
//
//    public long getProjectId() {
//        return projectId;
//    }
//
//    public void setProjectId(long projectId) {
//        this.projectId = projectId;
//    }
//
//    public String getJvmOptions() {
//        return jvmOptions;
//    }
//
//    public void setJvmOptions(String jvmOptions) {
//        this.jvmOptions = jvmOptions;
//    }
//
//    public String getParams() {
//        return params;
//    }
//
//    public void setParams(String params) {
//        this.params = params;
//    }
//
//    public int getExecResult() {
//        return execResult;
//    }
//
//    public void setExecResult(int execResult) {
//        this.execResult = execResult;
//    }
//}

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

        for(QueueEntryModel entry : entries) {
            entriesWithResult.add(QueueEntryWithResult.create(entry));
        }

        return entriesWithResult;
    }

    public static List<Map<String, Object>> createMapList(List<QueueEntryModel> entries) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for(QueueEntryWithResult entryWithResult : QueueEntryWithResult.createList(entries)) {
            mapList.add(entryWithResult.toMap());
        }

        return mapList;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsMap = new HashMap<>();

        for(Map.Entry<String, Object> field : this.entryModel.getAttrsEntrySet()) {
            fieldsMap.put(field.getKey(), field.getValue());
        }

        if(this.testResultModel == null) {
            fieldsMap.put(TestResultModel.Fields.EXEC_RESULT, ExecResult.UNKNOWN.getValue());
        }else {
            fieldsMap.put(TestResultModel.Fields.EXEC_RESULT, testResultModel.getExecResult());
        }

        return fieldsMap;
    }
}
