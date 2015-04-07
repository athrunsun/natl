package net.nitrogen.ates.core.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;
import net.nitrogen.ates.util.DateTimeUtil;
import net.nitrogen.ates.util.StringUtil;

import org.joda.time.DateTime;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class QueueEntryModel extends Model<QueueEntryModel> {
    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final String TABLE = "queue_entry";

    public class Fields {
        public static final String ID = "id";
        public static final String STATUS = "status";
        public static final String NAME = "name";
        public static final String SLAVE_NAME = "slave_name";
        public static final String INDEX = "index";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String EXECUTION_ID = "execution_id";
        public static final String PROJECT_ID = "project_id";
        @Deprecated
        public static final String ENV = "env";
        @Deprecated
        public static final String JVM_OPTIONS = "jvm_options";
        @Deprecated
        public static final String PARAMS = "params";
    }

    public static final QueueEntryModel me = new QueueEntryModel();

    public static QueueEntryModel createByDbRecord(Record record) {
        QueueEntryModel entry = new QueueEntryModel();
        entry.setId(record.getLong(Fields.ID));
        entry.setStatus(record.getInt(Fields.STATUS));
        entry.setName(record.getStr(Fields.NAME));
        entry.setSlaveName(record.getStr(Fields.SLAVE_NAME));
        entry.setIndex(record.getInt(Fields.INDEX));
        entry.setStartTimestamp(record.getTimestamp(Fields.START_TIME));
        entry.setEndTimestamp(record.getTimestamp(Fields.END_TIME));
        entry.setExecutionId(record.getLong(Fields.EXECUTION_ID));
        entry.setProjectId(record.getLong(Fields.PROJECT_ID));
        entry.setEnv(record.getStr(Fields.ENV));
        entry.setJvmOptions(record.getStr(Fields.JVM_OPTIONS));
        entry.setParams(record.getStr(Fields.PARAMS));
        return entry;
    }

    public static QueueEntryModel createByResultSet(ResultSet rs) {
        QueueEntryModel entry = new QueueEntryModel();

        try {
            entry.setId(rs.getLong(Fields.ID));
            entry.setStatus(rs.getInt(Fields.STATUS));
            entry.setName(rs.getString(Fields.NAME));
            entry.setSlaveName(rs.getString(Fields.SLAVE_NAME));
            entry.setIndex(rs.getInt(Fields.INDEX));
            entry.setStartTimestamp(rs.getTimestamp(Fields.START_TIME));
            entry.setEndTimestamp(rs.getTimestamp(Fields.END_TIME));
            entry.setExecutionId(rs.getLong(Fields.EXECUTION_ID));
            entry.setProjectId(rs.getLong(Fields.PROJECT_ID));
            entry.setEnv(rs.getString(Fields.ENV));
            entry.setJvmOptions(rs.getString(Fields.JVM_OPTIONS));
            entry.setParams(rs.getString(Fields.PARAMS));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entry;
    }

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public int getStatus() {
        return this.getInt(Fields.STATUS);
    }

    public void setStatus(int status) {
        this.set(Fields.STATUS, status);
    }

    public String getName() {
        return this.getStr(Fields.NAME);
    }

    public String getShortName() {
        return StringUtil.shortenString(this.getName(), TestCaseModel.MAX_TEST_NAME_LENGTH, false);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public String getSlaveName() {
        return this.getStr(Fields.SLAVE_NAME);
    }

    public void setSlaveName(String slaveName) {
        this.set(Fields.SLAVE_NAME, slaveName);
    }

    public int getIndex() {
        return this.getInt(Fields.INDEX);
    }

    public void setIndex(int index) {
        this.set(Fields.INDEX, index);
    }

    public Timestamp getStartTimestamp() {
        return this.getTimestamp(Fields.START_TIME);
    }

    public DateTime getStartTime() {
        Timestamp startTimestamp = this.getStartTimestamp();
        return startTimestamp == null ? null : DateTimeUtil.fromSqlTimestamp(startTimestamp);
    }

    public void setStartTimestamp(Timestamp t) {
        this.set(Fields.START_TIME, t);
    }

    public void setStartTime(DateTime startTime) {
        this.setStartTimestamp(DateTimeUtil.toSqlTimestamp(startTime));
    }

    public Timestamp getEndTimestamp() {
        return this.getTimestamp(Fields.END_TIME);
    }

    public DateTime getEndTime() {
        Timestamp endTimestamp = this.getEndTimestamp();
        return endTimestamp == null ? null : DateTimeUtil.fromSqlTimestamp(endTimestamp);
    }

    public void setEndTimestamp(Timestamp t) {
        this.set(Fields.END_TIME, t);
    }

    public void setEndTime(DateTime endTime) {
        this.setEndTimestamp(DateTimeUtil.toSqlTimestamp(endTime));
    }

    public long getExecutionId() {
        return this.getLong(Fields.EXECUTION_ID);
    }

    public void setExecutionId(long executionId) {
        this.set(Fields.EXECUTION_ID, executionId);
    }

    public long getProjectId() {
        return this.getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }

    @Deprecated
    public String getEnv() {
        return this.getStr(Fields.ENV);
    }

    @Deprecated
    public void setEnv(String env) {
        this.set(Fields.ENV, env);
    }

    public String getJvmOptions() {
        return CustomParameterModel.me.getJvmParametersAsString(CustomParameterDomainKey.EXECUTION, getExecutionId());
    }

    @Deprecated
    public void setJvmOptions(String jvmOptions) {
        this.set(Fields.JVM_OPTIONS, jvmOptions);
    }

    @Deprecated
    public String getParams() {
        return this.getStr(Fields.PARAMS);
    }

    @Deprecated
    public void setParams(String params) {
        this.set(Fields.PARAMS, params);
    }

    public long allEntriesPageCount() {
        return this.allEntriesPageCount(DEFAULT_PAGE_SIZE);
    }

    public long allEntriesPageCount(int pageSize) {
        long total = Db.queryLong(String.format("SELECT COUNT(`%s`) FROM `%s`", Fields.ID, TABLE));
        return total / pageSize + ((total % pageSize <= 0) ? 0 : 1);
    }

    public long entriesPageCount(long executionId) {
        return this.entriesPageCount(executionId, DEFAULT_PAGE_SIZE);
    }

    public long entriesPageCount(long executionId, int pageSize) {
        long total = Db.queryLong(String.format("SELECT COUNT(`%s`) FROM `%s` WHERE `%s`=?", Fields.ID, TABLE, Fields.EXECUTION_ID), executionId);
        return total / pageSize + ((total % pageSize <= 0) ? 0 : 1);
    }

    public List<QueueEntryModel> findAllEntries() {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.NAME,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                Fields.JVM_OPTIONS,
                Fields.PARAMS,
                TABLE,
                Fields.ID));
    }

    public List<QueueEntryModel> findEntries(long executionId) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.NAME,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                Fields.JVM_OPTIONS,
                Fields.PARAMS,
                TABLE,
                Fields.EXECUTION_ID,
                Fields.ID);

        return find(sql, executionId);
    }

    public Page<QueueEntryModel> paginate(int pageNumber, int pageSize) {
        String selectClause = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.NAME,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                Fields.JVM_OPTIONS,
                Fields.PARAMS);

        String sqlExceptSelect = String.format("FROM `%s` ORDER BY `%s` DESC", TABLE, Fields.ID);

        return paginate(pageNumber, pageSize, selectClause, sqlExceptSelect);
    }

    public List<QueueEntryModel> findEntries(long executionId, ExecResult execResult) {
        return (List<QueueEntryModel>) Db.execute(new FindQueueEntriesByExecResultCallback(executionId, execResult));
    }

    public void insertEntries(List<QueueEntryModel> entries) {
        List<QueueEntryModel> foundEntries = new ArrayList<QueueEntryModel>();

        // Make sure all entries have corresponding test cases
        for (QueueEntryModel entry : entries) {
            // Test case name (i.e. test method name) is the unique identifier
            TestCaseModel foundTestCase = TestCaseModel.me.findFirstTestCase(entry.getProjectId(), entry.getName());

            if (foundTestCase != null) {
                foundEntries.add(entry);
            }
        }

        final int PARAMS_SIZE = 8;

        if (foundEntries.size() > 0) {
            final String sql = String.format(
                    "INSERT INTO `%s`(`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`) VALUES(?,?,?,?,?,?,?,?)",
                    TABLE,
                    Fields.STATUS,
                    Fields.NAME,
                    Fields.SLAVE_NAME,
                    Fields.EXECUTION_ID,
                    Fields.PROJECT_ID,
                    Fields.ENV,
                    Fields.JVM_OPTIONS,
                    Fields.PARAMS);

            final Object[][] params = new Object[foundEntries.size()][PARAMS_SIZE];

            for (int i = 0; i < foundEntries.size(); i++) {
                params[i][0] = foundEntries.get(i).getStatus();
                params[i][1] = foundEntries.get(i).getName();
                params[i][2] = foundEntries.get(i).getSlaveName();
                params[i][3] = foundEntries.get(i).getExecutionId();
                params[i][4] = foundEntries.get(i).getProjectId();
                params[i][5] = foundEntries.get(i).getEnv();
                params[i][6] = foundEntries.get(i).getJvmOptions();
                params[i][7] = foundEntries.get(i).getParams();
            }

            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    Db.batch(sql, params, 500);
                    return true;
                }
            });
        }
    }

    public QueueEntryModel fetchEntry(final String slaveName) {
        Long entryId = (Long) (Db.execute(new FetchQueueEntryCallback(slaveName)));

        if (entryId != null && entryId.longValue() > 0) {
            return findById(entryId.longValue());
        } else {
            return null;
        }
    }

    public void markEntryAsFinished(long entryId) {
        this.me.findById(entryId).set(Fields.STATUS, QueueEntryStatus.FINISHED.getStatus())
                .set(Fields.END_TIME, DateTimeUtil.toStringWithDefaultFormat(DateTime.now())).update();
    }
}
