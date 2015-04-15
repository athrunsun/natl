package net.nitrogen.ates.core.model;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;

public class TestGroupTestCaseModel extends Model<TestGroupTestCaseModel> {
    public static final String TABLE = "test_group-test_case";

    public class Fields {
        public static final String ID = "id";
        public static final String TEST_GROUP_ID = "test_group_id";
        public static final String TEST_CASE_ID = "test_case_id";
    }

    public static final TestGroupTestCaseModel me = new TestGroupTestCaseModel();

    public long getId() {
        return getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public long getTestGroupId() {
        return getLong(Fields.TEST_GROUP_ID);
    }

    public void setTestGroupId(long testGroupId) {
        this.set(Fields.TEST_GROUP_ID, testGroupId);
    }

    public long getTestCaseId() {
        return getLong(Fields.TEST_CASE_ID);
    }

    public void setTestCaseId(long testCaseId) {
        this.set(Fields.TEST_CASE_ID, testCaseId);
    }

    public List<TestGroupTestCaseModel> findTestGroupTestCases(long testGroupId) {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?",
                Fields.ID,
                Fields.TEST_GROUP_ID,
                Fields.TEST_CASE_ID,
                TABLE,
                Fields.TEST_GROUP_ID), testGroupId);
    }

    public void insertTestGroupTestCases(List<TestGroupTestCaseModel> testGroupTestCases) {
        final int INSERT_TEST_GROUP_TEST_CASE_PARAMS_SIZE = 2;
        final String insertTestGroupTestCaseSql = String.format(
                "INSERT `%s`(`%s`,`%s`) VALUES(?,?)",
                TestGroupTestCaseModel.TABLE,
                TestGroupTestCaseModel.Fields.TEST_GROUP_ID,
                TestGroupTestCaseModel.Fields.TEST_CASE_ID);
        final Object[][] insertTestGroupTestCaseParams = new Object[testGroupTestCases.size()][INSERT_TEST_GROUP_TEST_CASE_PARAMS_SIZE];

        for (int i = 0; i < testGroupTestCases.size(); i++) {
            insertTestGroupTestCaseParams[i][0] = testGroupTestCases.get(i).getTestGroupId();
            insertTestGroupTestCaseParams[i][1] = testGroupTestCases.get(i).getTestCaseId();
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                // Insert new test_group-test_case records
                Db.batch(insertTestGroupTestCaseSql, insertTestGroupTestCaseParams, 500);
                return true;
            }
        });
    }
}
