package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.TestCase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestCaseModel extends Model<TestCaseModel> {
    public static final String TABLE = "test_case";

    public class Fields {
        public static final String PROJECT_ID = "project_id";
        public static final String NAME = "name";
        public static final String MAPPING_ID = "mapping_id";
    }

    public static final TestCaseModel me = new TestCaseModel();

    public TestCase findFirstTestCase(long projectId, String name) {
        TestCaseModel m = this.findFirst(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? AND `%s`=? LIMIT 1", Fields.PROJECT_ID, Fields.MAPPING_ID, Fields.NAME, TABLE, Fields.PROJECT_ID, Fields.NAME),
                projectId,
                name);

        if (m != null) {
            return TestCase.create(m);
        }

        return null;
    }

    public List<TestCase> findTestCases(long projectId) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?",
                Fields.PROJECT_ID,
                Fields.NAME,
                Fields.MAPPING_ID,
                TABLE,
                Fields.PROJECT_ID);

        List<TestCase> testCases = new ArrayList<>();

        for(TestCaseModel m : find(sql, projectId)) {
            testCases.add(TestCase.create(m));
        }

        return testCases;
    }

    public void reloadTestCases(final long projectId, List<TestCase> testCases) {
        final String deleteSql = String.format("DELETE FROM `%s` WHERE `%s`=?", TABLE, Fields.PROJECT_ID);

        final int INSERT_PARAMS_SIZE = 3;
        final String insertSql = String.format("INSERT `%s`(`%s`,`%s`,`%s`) VALUES(?,?,?)", TABLE, Fields.PROJECT_ID, Fields.MAPPING_ID, Fields.NAME);
        final Object[][] insertParams = new Object[testCases.size()][INSERT_PARAMS_SIZE];

        for (int i = 0; i < testCases.size(); i++) {
            insertParams[i][0] = testCases.get(i).getProjectId();
            insertParams[i][1] = testCases.get(i).getMappingId();
            insertParams[i][2] = testCases.get(i).getName();
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.update(deleteSql, projectId);
                Db.batch(insertSql, insertParams, 500);
                return true;
            }
        });
    }
}
