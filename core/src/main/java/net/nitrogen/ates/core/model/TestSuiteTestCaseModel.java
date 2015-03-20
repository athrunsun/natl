package net.nitrogen.ates.core.model;

import net.nitrogen.ates.util.StringUtil;

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

    public String getTestName() {
        return getStr(Fields.TEST_NAME);
    }

    public String getShortTestName() {
        return StringUtil.shortenString(this.getTestName(), TestCaseModel.MAX_TEST_NAME_LENGTH, false);
    }

    public void setTestName(String testName) {
        this.set(Fields.TEST_NAME, testName);
    }

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
