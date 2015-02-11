package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.QueueEntry;
import net.nitrogen.ates.core.entity.TestCase;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;
import net.nitrogen.ates.util.DateTimeUtil;
import org.joda.time.DateTime;

import java.sql.*;
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
        public static final String ROUND_ID = "round_id";
        public static final String PROJECT_ID = "project_id";
        public static final String ENV = "env";
        public static final String JVM_OPTIONS = "jvm_options";
        public static final String PARAMS = "params";
    }

    public static final QueueEntryModel me = new QueueEntryModel();

    public List<QueueEntryModel> findAllEntriesAsModelList() {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.NAME,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.ROUND_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                Fields.JVM_OPTIONS,
                Fields.PARAMS,
                TABLE,
                Fields.ID));
    }

    public List<QueueEntry> findAllEntries() {
        List<QueueEntryModel> entryModelList = this.findAllEntriesAsModelList();
        List<QueueEntry> entries = new ArrayList<QueueEntry>();

        for (QueueEntryModel m : entryModelList) {
            entries.add(QueueEntry.create(m));
        }

        return entries;
    }

    public List<QueueEntryModel> findEntriesAsModelList(long roundId) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC",
                Fields.ID,
                Fields.STATUS,
                Fields.NAME,
                Fields.SLAVE_NAME,
                Fields.INDEX,
                Fields.START_TIME,
                Fields.END_TIME,
                Fields.ROUND_ID,
                Fields.PROJECT_ID,
                Fields.ENV,
                Fields.JVM_OPTIONS,
                Fields.PARAMS,
                TABLE,
                Fields.ROUND_ID,
                Fields.ID);

        return find(sql, roundId);
    }

    public void insertEntries(List<QueueEntry> entries) {
        List<QueueEntry> foundEntries = new ArrayList<QueueEntry>();

        // Make sure all entries have corresponding test cases
        for (QueueEntry entry : entries) {
            // Test case name (i.e. test method name) is the unique identifier
            TestCase foundTestCase = TestCaseModel.me.findFirstTestCase(entry.getProjectId(), entry.getName());

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
                    Fields.ROUND_ID,
                    Fields.PROJECT_ID,
                    Fields.ENV,
                    Fields.JVM_OPTIONS,
                    Fields.PARAMS);

            final Object[][] params = new Object[foundEntries.size()][PARAMS_SIZE];

            for (int i = 0; i < foundEntries.size(); i++) {
                params[i][0] = foundEntries.get(i).getStatus();
                params[i][1] = foundEntries.get(i).getName();
                params[i][2] = foundEntries.get(i).getSlaveName();
                params[i][3] = foundEntries.get(i).getRoundId();
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

    public QueueEntry fetchEntry(final String slaveName) {
        Long entryId = (Long)(Db.execute(new FetchQueueEntryCallback(slaveName)));
//        Connection dbConnection = null;
//        dbConnection = getDBConnection();
//        Long entryId = null;
//
//        try {
//            entryId = (Long)(new FetchQueueEntryCallback(slaveName).call(dbConnection));
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//            try {
//                dbConnection.rollback();
//                dbConnection.close();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//        }

        if (entryId != null && entryId.longValue() > 0) {
            return QueueEntry.create(findById(entryId.longValue()));
        } else {
            return null;
        }
    }

    private static Connection getDBConnection() {
        Connection dbConnection = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/nitrogenates?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", "nitrogenadmin", "@ctive123");
            return dbConnection;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dbConnection;
    }

    public void markEntryAsFinished(long entryId) {
        this.me.findById(entryId).set(Fields.STATUS, QueueEntryStatus.FINISHED.getStatus()).set(Fields.END_TIME, DateTimeUtil.toStringWithDefaultFormat(DateTime.now())).update();
    }
}
