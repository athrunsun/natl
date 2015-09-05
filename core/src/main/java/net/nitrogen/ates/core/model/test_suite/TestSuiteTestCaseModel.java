package net.nitrogen.ates.core.model.test_suite;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.model.project.ProjectModel;
import net.nitrogen.ates.core.model.test_case.TestCaseModel;

public class TestSuiteTestCaseModel extends Model<TestSuiteTestCaseModel> {
    public static final String TABLE = "test_suite-test_case";

    public class Fields {
        public static final String ID = "id";
        public static final String TEST_SUITE_ID = "test_suite_id";
        public static final String TEST_CASE_ID = "test_case_id";
    }

    public static final TestSuiteTestCaseModel me = new TestSuiteTestCaseModel();

    public long getId() {
        return getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public long getTestSuiteId() {
        return getLong(Fields.TEST_SUITE_ID);
    }

    public void setTestSuiteId(long testSuiteId) {
        this.set(Fields.TEST_SUITE_ID, testSuiteId);
    }

    public long getTestCaseId() {
        return getLong(Fields.TEST_CASE_ID);
    }

    public void setTestCaseId(long testCaseId) {
        this.set(Fields.TEST_CASE_ID, testCaseId);
    }

    public List<TestSuiteTestCaseModel> findTestSuiteTestCases(long testSuiteId) {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?",
                Fields.ID,
                Fields.TEST_SUITE_ID,
                Fields.TEST_CASE_ID,
                TABLE,
                Fields.TEST_SUITE_ID), testSuiteId);
    }

    public int delete(long suiteId, long testCaseId) {
        String sql = String.format(
                "DELETE FROM `%s` WHERE `%s`=? AND `%s` =?;",
                TestSuiteTestCaseModel.TABLE,
                TestSuiteTestCaseModel.Fields.TEST_SUITE_ID,
                TestSuiteTestCaseModel.Fields.TEST_CASE_ID);

        return Db.update(sql, suiteId, testCaseId);
    }

    public void deleteNonexistent(long projectId) {
        long caseVersion = ProjectModel.me.findLatestTestCaseVersionForProject(projectId);
        String sql = String.format(
                "DELETE `ts-tc` FROM `%s` AS `ts-tc` LEFT JOIN `%s` AS `tc` ON `ts-tc`.`%s`=`tc`.`%s` WHERE `tc`.`%s`=? AND `tc`.`%s`<>?",
                TestSuiteTestCaseModel.TABLE,
                TestCaseModel.TABLE,
                TestSuiteTestCaseModel.Fields.TEST_CASE_ID,
                TestCaseModel.Fields.ID,
                TestCaseModel.Fields.PROJECT_ID,
                TestCaseModel.Fields.VERSION);
        Db.update(sql, projectId, caseVersion);
    }

    public void insertTestSuiteTestCasesIfNotExists(List<TestSuiteTestCaseModel> testSuiteTestCases) {
        final int INSERT_TEST_SUITE_TEST_CASE_PARAMS_SIZE = 4;
        final String insertTestSuiteTestCaseSql = String.format(
                "INSERT INTO `%s` (`%s`,`%s`) SELECT ?, ? FROM dual WHERE ",
                TestSuiteTestCaseModel.TABLE,
                TestSuiteTestCaseModel.Fields.TEST_SUITE_ID,
                TestSuiteTestCaseModel.Fields.TEST_CASE_ID)
                + String.format(
                        "NOT EXISTS(SELECT * FROM `%s` WHERE `%s`=? AND `%s`=?);",
                        TestSuiteTestCaseModel.TABLE,
                        TestSuiteTestCaseModel.Fields.TEST_SUITE_ID,
                        TestSuiteTestCaseModel.Fields.TEST_CASE_ID);
        final Object[][] insertTestSuiteTestCaseParams = new Object[testSuiteTestCases.size()][INSERT_TEST_SUITE_TEST_CASE_PARAMS_SIZE];

        for (int i = 0; i < testSuiteTestCases.size(); i++) {
            insertTestSuiteTestCaseParams[i][0] = testSuiteTestCases.get(i).getTestSuiteId();
            insertTestSuiteTestCaseParams[i][1] = testSuiteTestCases.get(i).getTestCaseId();
            insertTestSuiteTestCaseParams[i][2] = testSuiteTestCases.get(i).getTestSuiteId();
            insertTestSuiteTestCaseParams[i][3] = testSuiteTestCases.get(i).getTestCaseId();
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                // Insert new test_suite-test_case records
                Db.batch(insertTestSuiteTestCaseSql, insertTestSuiteTestCaseParams, 500);
                return true;
            }
        });
    }
}
