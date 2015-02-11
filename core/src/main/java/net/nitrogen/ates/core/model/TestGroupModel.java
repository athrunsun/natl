package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.TestGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestGroupModel extends Model<TestGroupModel> {
    public static final String TABLE = "test_group";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final TestGroupModel me = new TestGroupModel();

    public TestGroup findTestGroup(long testGroupId){
        TestGroupModel m = findById(testGroupId);
        return TestGroup.create(m);
    }

    public List<TestGroup> findTestGroups(long projectId) {
        List<TestGroup> testGroups = new ArrayList<TestGroup>();

        List<TestGroupModel> mList = find(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.PROJECT_ID),
                projectId);

        for(TestGroupModel m : mList) {
            testGroups.add(TestGroup.create(m));
        }

        return testGroups;
    }

    public void insertTestGroup(TestGroup testGroup) {
        TestGroupModel m = new TestGroupModel();
        m.set(Fields.NAME, testGroup.getName()).set(Fields.PROJECT_ID, testGroup.getProjectId()).save();
        testGroup.setId(m.getInt(Fields.ID));
    }

    public void insertTestGroups(List<TestGroup> testGroups) {
        final int INSERT_TEST_GROUP_PARAMS_SIZE = 2;
        final String insertTestGroupSql = String.format("INSERT `%s`(`%s`,`%s`) VALUES(?,?)", TABLE, Fields.NAME, Fields.PROJECT_ID);
        final Object[][] insertTestGroupParams = new Object[testGroups.size()][INSERT_TEST_GROUP_PARAMS_SIZE];

        for (int i = 0; i < testGroups.size(); i++) {
            insertTestGroupParams[i][0] = testGroups.get(i).getName();
            insertTestGroupParams[i][1] = testGroups.get(i).getProjectId();
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                // Insert new test_group records
                Db.batch(insertTestGroupSql, insertTestGroupParams, 500);
                return true;
            }
        });
    }

    public void deleteTestGroupsAndRespectiveTestGroupTestCases(final long projectId) {
        final String deleteTestGroupTestCaseSql = String.format(
                "DELETE `tg-tc` FROM `%s` AS `tg-tc` INNER JOIN `%s` AS `tg` ON `tg`.`%s`=`tg-tc`.`%s` WHERE `tg`.`%s`=?",
                TestGroupTestCaseModel.TABLE,
                TestGroupModel.TABLE,
                TestGroupModel.Fields.ID,
                TestGroupTestCaseModel.Fields.TEST_GROUP_ID,
                TestGroupModel.Fields.PROJECT_ID);

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                // Delete existing test_group-test_case records
                Db.update(deleteTestGroupTestCaseSql, projectId);

                // Then delete existing test_group records
                Db.update(String.format("DELETE FROM `%s` WHERE `%s`=?", TABLE, Fields.PROJECT_ID), projectId);
                return true;
            }
        });
    }
}
