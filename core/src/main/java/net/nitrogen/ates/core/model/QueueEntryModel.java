package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;
import net.nitrogen.ates.util.DateTimeUtil;
import net.nitrogen.ates.util.StringUtil;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QueueEntryModel extends Model<QueueEntryModel> {
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
        public static final String ENV = "env";
        public static final String JVM_OPTIONS = "jvm_options";
        public static final String PARAMS = "params";
    }

    public static final QueueEntryModel me = new QueueEntryModel();

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

    public String getEnv() {
        return this.getStr(Fields.ENV);
    }

    public void setEnv(String env) {
        this.set(Fields.ENV, env);
    }

    public String getJvmOptions() {
        return this.getStr(Fields.JVM_OPTIONS);
    }

    public void setJvmOptions(String jvmOptions) {
        this.set(Fields.JVM_OPTIONS, jvmOptions);
    }

    public String getParams() {
        return this.getStr(Fields.PARAMS);
    }

    public void setParams(String params) {
        this.set(Fields.PARAMS, params);
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

        // For debugging purpose
        // Connection dbConnection = null;
        // dbConnection = getDBConnection();
        // Long entryId = null;
        //
        // try {
        // entryId = (Long)(new FetchQueueEntryCallback(slaveName).call(dbConnection));
        // } catch (SQLException e) {
        // System.out.println(e.getMessage());
        // try {
        // dbConnection.rollback();
        // dbConnection.close();
        // } catch (SQLException e1) {
        // e1.printStackTrace();
        // }
        // }

        if (entryId != null && entryId.longValue() > 0) {
            return findById(entryId.longValue());
        } else {
            return null;
        }
    }

    // For debugging purpose
    private static Connection getDBConnection() {
        Connection dbConnection = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/nitrogenates?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull",
                    "nitrogenadmin",
                    "@ctive123");
            return dbConnection;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dbConnection;
    }

    public void markEntryAsFinished(long entryId) {
        this.me.findById(entryId).set(Fields.STATUS, QueueEntryStatus.FINISHED.getStatus())
                .set(Fields.END_TIME, DateTimeUtil.toStringWithDefaultFormat(DateTime.now())).update();
    }
}
