package net.nitrogen.ates.core.model.queue_entry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.*;
import net.nitrogen.ates.core.callback.Callback_FindValidQueueEntriesForExecution_ExecResultFiltering;
import net.nitrogen.ates.core.callback.FetchQueueEntryCallback;
import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;
import net.nitrogen.ates.core.model.test_case.TestCaseModel;
import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.util.DateTimeUtil;

import org.joda.time.DateTime;

public class QueueEntryModel extends Model<QueueEntryModel> {
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String TABLE = "queue_entry";

    public class Fields {
        public static final String ID = "id";
        public static final String STATUS = "status";
        public static final String TEST_CASE_ID = "test_case_id";
        public static final String SLAVE_NAME = "slave_name";
        public static final String INDEX = "index";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String EXECUTION_ID = "execution_id";
        public static final String PROJECT_ID = "project_id";
    }

    public static final QueueEntryModel me = new QueueEntryModel();

    public static QueueEntryModel createByDbRecord(Record record) {
        QueueEntryModel entry = new QueueEntryModel();
        entry.setId(record.getLong(Fields.ID));
        entry.setStatus(record.getInt(Fields.STATUS));
        entry.setTestCaseId(record.getLong(Fields.TEST_CASE_ID));
        entry.setSlaveName(record.getStr(Fields.SLAVE_NAME));
        entry.setIndex(record.getInt(Fields.INDEX));
        entry.setStartTimestamp(record.getTimestamp(Fields.START_TIME));
        entry.setEndTimestamp(record.getTimestamp(Fields.END_TIME));
        entry.setExecutionId(record.getLong(Fields.EXECUTION_ID));
        entry.setProjectId(record.getLong(Fields.PROJECT_ID));
        return entry;
    }

    public static QueueEntryModel createByResultSet(ResultSet rs) {
        QueueEntryModel entry = new QueueEntryModel();

        try {
            entry.setId(rs.getLong(Fields.ID));
            entry.setStatus(rs.getInt(Fields.STATUS));
            entry.setTestCaseId(rs.getLong(Fields.TEST_CASE_ID));
            entry.setSlaveName(rs.getString(Fields.SLAVE_NAME));
            entry.setIndex(rs.getInt(Fields.INDEX));
            entry.setStartTimestamp(rs.getTimestamp(Fields.START_TIME));
            entry.setEndTimestamp(rs.getTimestamp(Fields.END_TIME));
            entry.setExecutionId(rs.getLong(Fields.EXECUTION_ID));
            entry.setProjectId(rs.getLong(Fields.PROJECT_ID));
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

    public long getTestCaseId() {
        return this.getLong(Fields.TEST_CASE_ID);
    }

    public void setTestCaseId(long id) {
        this.set(Fields.TEST_CASE_ID, id);
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

    public String getJvmOptionsAsString() {
        return CustomParameterModel.me.getJvmParametersAsString(CustomParameterDomainKey.EXECUTION, getExecutionId());
    }

    public long allEntriesPageCount() {
        return this.allEntriesPageCount(DEFAULT_PAGE_SIZE);
    }

    public long allEntriesPageCount(int pageSize) {
        long total = Db.queryLong(String.format("SELECT COUNT(`%s`) FROM `%s`", Fields.ID, TABLE));
        return total / pageSize + ((total % pageSize <= 0) ? 0 : 1);
    }

    public long entriesPageCountForExecution(long executionId) {
        return this.entriesPageCountForExecution(executionId, DEFAULT_PAGE_SIZE);
    }

    public long entriesPageCountForExecution(long executionId, int pageSize) {
        long total = Db.queryLong(String.format("SELECT COUNT(`%s`) FROM `%s` WHERE `%s`=?", Fields.ID, TABLE, Fields.EXECUTION_ID), executionId);
        return total / pageSize + ((total % pageSize <= 0) ? 0 : 1);
    }

    public long entriesPageCountForProject(long projectId) {
        return this.entriesPageCountForProject(projectId, DEFAULT_PAGE_SIZE);
    }

    public long entriesPageCountForProject(long projectId, int pageSize) {
        long total = Db.queryLong(String.format("SELECT COUNT(`%s`) FROM `%s` WHERE `%s`=?", Fields.ID, TABLE, Fields.PROJECT_ID), projectId);
        return total / pageSize + ((total % pageSize <= 0) ? 0 : 1);
    }

    public List<QueueEntryModel> findAllEntries() {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.TEST_CASE_ID,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                TABLE,
                Fields.ID));
    }

    public List<QueueEntryModel> findEntriesForExecution(long executionId) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.TEST_CASE_ID,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID,
                TABLE,
                Fields.EXECUTION_ID,
                Fields.ID);

        return find(sql, executionId);
    }

    @SuppressWarnings("unchecked")
    public List<QueueEntryModel> findValidEntriesForExecution(final long executionId) {
        return (List<QueueEntryModel>) Db.execute(new ICallback() {
            @Override
            public Object call(Connection conn) throws SQLException {
                CallableStatement callSP = null;
                List<QueueEntryModel> entries = new ArrayList<>();

                try {
                    callSP = conn.prepareCall("{CALL FindValidQueueEntriesForExecution(?)}");
                    callSP.setLong(1, executionId);
                    boolean hadResults = callSP.execute();

                    if (hadResults) {
                        ResultSet rs = callSP.getResultSet();
                        rs.beforeFirst();

                        while (rs.next()) {
                            entries.add(QueueEntryModel.createByResultSet(rs));
                        }

                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (callSP != null) {
                        callSP.close();
                    }
                }

                return entries;
            }
        });
    }

    public Page<QueueEntryModel> paginate(int pageNumber, int pageSize) {
        String selectClause = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.TEST_CASE_ID,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.EXECUTION_ID,
                Fields.PROJECT_ID);

        String sqlExceptSelect = String.format("FROM `%s` ORDER BY `%s` DESC", TABLE, Fields.ID);

        return paginate(pageNumber, pageSize, selectClause, sqlExceptSelect);
    }

    public List<QueueEntryModel> findValidEntriesForExecution(long executionId, ExecResult execResult) {
        return (List<QueueEntryModel>) Db.execute(new Callback_FindValidQueueEntriesForExecution_ExecResultFiltering(executionId, execResult));
    }

    public void insertEntries(List<QueueEntryModel> entries) {
        List<QueueEntryModel> foundEntries = new ArrayList<QueueEntryModel>();

        // Make sure all entries have corresponding test cases
        for (QueueEntryModel entry : entries) {
            // Test case id is the unique identifier
            TestCaseModel foundTestCase = TestCaseModel.me.findValidTestCase(entry.getProjectId(), entry.getTestCaseId());

            if (foundTestCase != null) {
                foundEntries.add(entry);
            }
        }

        final int PARAMS_SIZE = 5;

        if (foundEntries.size() > 0) {
            final String sql = String.format(
                    "INSERT INTO `%s`(`%s`,`%s`,`%s`,`%s`,`%s`) VALUES(?,?,?,?,?)",
                    TABLE,
                    Fields.STATUS,
                    Fields.TEST_CASE_ID,
                    Fields.SLAVE_NAME,
                    Fields.EXECUTION_ID,
                    Fields.PROJECT_ID);

            final Object[][] params = new Object[foundEntries.size()][PARAMS_SIZE];

            for (int i = 0; i < foundEntries.size(); i++) {
                params[i][0] = foundEntries.get(i).getStatus();
                params[i][1] = foundEntries.get(i).getTestCaseId();
                params[i][2] = foundEntries.get(i).getSlaveName();
                params[i][3] = foundEntries.get(i).getExecutionId();
                params[i][4] = foundEntries.get(i).getProjectId();
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
