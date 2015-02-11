package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class TestSuiteTestCaseModel extends Model<TestSuiteTestCaseModel> {
    public static final String TABLE = "test_suite-test_case";

    public class Fields {
        public static final String ID = "id";
        public static final String TEST_SUITE_ID = "test_suite_id";
        public static final String TEST_NAME = "test_name";
    }

    public static final TestSuiteTestCaseModel me = new TestSuiteTestCaseModel();

    public void deleteNonexistent(long projectId) {
        String sql = String.format(
                "DELETE `ts-tc` FROM `%s` AS `ts-tc` LEFT JOIN `%s` AS `tc` ON `ts-tc`.`%s`=`tc`.`%s` WHERE `tc`.`%s`=? AND `tc`.`%s` IS NULL",
                TestSuiteTestCaseModel.TABLE,
                TestCaseModel.TABLE,
                TestSuiteTestCaseModel.Fields.TEST_NAME,
                TestCaseModel.Fields.NAME,
                TestCaseModel.Fields.PROJECT_ID,
                TestCaseModel.Fields.NAME);

        Db.update(sql, projectId);
    }
}
