package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.util.DateTimeUtil;
import net.nitrogen.ates.util.StringUtil;
import org.joda.time.DateTime;

public class QueueEntry {
    private long id;
    private int status;
    private String name;
    private String slaveName;
    private int index;
    private DateTime startTime;
    private DateTime endTime;
    private long roundId;
    private long projectId;
    private String env;
    private String jvmOptions;
    private String params;

    public static QueueEntry create(QueueEntryModel m) {
        QueueEntry q = new QueueEntry();
        q.setId(m.getLong(QueueEntryModel.Fields.ID));
        q.setStatus(m.getInt(QueueEntryModel.Fields.STATUS));
        q.setName(m.getStr(QueueEntryModel.Fields.NAME));
        q.setSlaveName(m.getStr(QueueEntryModel.Fields.SLAVE_NAME));
        q.setIndex(m.getInt(QueueEntryModel.Fields.INDEX));
        q.setStartTime(m.getTimestamp(QueueEntryModel.Fields.START_TIME) == null ? null : DateTimeUtil.fromSqlTimestamp(m.getTimestamp(QueueEntryModel.Fields.START_TIME)));
        q.setEndTime(m.getTimestamp(QueueEntryModel.Fields.END_TIME) == null ? null : DateTimeUtil.fromSqlTimestamp(m.getTimestamp(QueueEntryModel.Fields.END_TIME)));
        q.setRoundId(m.getLong(QueueEntryModel.Fields.ROUND_ID));
        q.setProjectId(m.getLong(QueueEntryModel.Fields.PROJECT_ID));
        q.setEnv(m.getStr(QueueEntryModel.Fields.ENV));
        q.setJvmOptions(m.getStr(QueueEntryModel.Fields.JVM_OPTIONS));
        q.setParams(m.getStr(QueueEntryModel.Fields.PARAMS));
        return q;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return StringUtil.shortenString(this.name, TestCase.MAX_TEST_NAME_LENGTH);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlaveName() {
        return slaveName;
    }

    public void setSlaveName(String slaveName) {
        this.slaveName = slaveName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
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

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
