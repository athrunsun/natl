package net.nitrogen.ates.core.model.test_case;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nitrogen.ates.core.model.project.ProjectModel;
import net.nitrogen.ates.util.StringUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;

public class TestCaseModel extends Model<TestCaseModel> {
    public static final int MAX_TEST_NAME_LENGTH = 80;
    public static final String TABLE = "test_case";

    public class Fields {
        public static final String ID = "id";
        public static final String PROJECT_ID = "project_id";
        public static final String NAME = "name";
        public static final String VERSION = "version";
        public static final String MAPPING_ID = "mapping_id";
    }

    public static final TestCaseModel me = new TestCaseModel();

    public static TestCaseModel createByResultSet(ResultSet rs) {
        TestCaseModel testCaseModel = new TestCaseModel();

        try {
            testCaseModel.setId(rs.getLong(Fields.ID));
            testCaseModel.setProjectId(rs.getLong(Fields.PROJECT_ID));
            testCaseModel.setName(rs.getString(Fields.NAME));
            testCaseModel.setMappingId(rs.getString(Fields.MAPPING_ID));
            testCaseModel.setVersion(rs.getLong(Fields.VERSION));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return testCaseModel;
    }

    public static long generateTestCaseVersion() {
        // 10 digital like: 1504101719
        return Long.parseLong(new SimpleDateFormat("yyMMddHHmm").format(Calendar.getInstance().getTime()));
    }

    public long getId() {
        return getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public long getProjectId() {
        return getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }

    public String getMappingId() {
        return getStr(Fields.MAPPING_ID);
    }

    public void setMappingId(String mappingId) {
        this.set(Fields.MAPPING_ID, mappingId);
    }

    public Long getVersion() {
        return getLong(Fields.VERSION);
    }

    public String getName() {
        return getStr(Fields.NAME);
    }

    public String getShortName() {
        return StringUtil.shortenString(this.getName(), TestCaseModel.MAX_TEST_NAME_LENGTH, false);
    }

    public String getHtmlEncodedName() {
        return StringEscapeUtils.escapeHtml4(this.getName());
    }

    public void setVersion(Long version) {
        this.set(Fields.VERSION, version);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public boolean isTestCaseValid(long testCaseId) {
        TestCaseModel testCase = findById(testCaseId);
        return testCase.getVersion() == ProjectModel.me.findLatestTestCaseVersionForProject(testCase.getProjectId());
    }

    public TestCaseModel findValidTestCase(long projectId, long testCaseId) {
        String sql = String.format(
                "SELECT `%s`, `%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? AND `%s`=? LIMIT 1",
                Fields.ID,
                Fields.NAME,
                Fields.PROJECT_ID,
                Fields.MAPPING_ID,
                Fields.VERSION,
                TABLE,
                Fields.ID,
                Fields.VERSION);

        return this.findFirst(sql, testCaseId, ProjectModel.me.findLatestTestCaseVersionForProject(projectId));
    }

    public TestCaseModel findValidTestCase(long projectId, String testCaseName) {
        String sql = String.format(
                "SELECT `%s`, `%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? AND `%s`=? LIMIT 1",
                Fields.ID,
                Fields.NAME,
                Fields.PROJECT_ID,
                Fields.MAPPING_ID,
                Fields.VERSION,
                TABLE,
                Fields.NAME,
                Fields.PROJECT_ID);

        return this.findFirst(sql, testCaseName, projectId);
    }

    public List<TestCaseModel> findValidTestCases(long projectId) {
        String sql = String.format(
                "SELECT `%s`, `%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? AND `%s`=?",
                Fields.ID,
                Fields.PROJECT_ID,
                Fields.NAME,
                Fields.VERSION,
                Fields.MAPPING_ID,
                TABLE,
                Fields.PROJECT_ID,
                Fields.VERSION);

        return find(sql, projectId, ProjectModel.me.findLatestTestCaseVersionForProject(projectId));
    }

    public void reloadTestCases(final long projectId, List<TestCaseModel> testCases) {
        long version = generateTestCaseVersion();
        Map<String, String> existingCases = findAllCaseNameIdMap(projectId);
        ProjectModel.me.updateLatestTestCaseVersionForProject(projectId, version);

        final int UPDATE_PARAMS_SIZE = 4;
        final int INSERT_PARAMS_SIZE = 4;
        final String updateSql = String.format(
                "UPDATE `%s` SET `%s`=?, `%s`=? WHERE `%s`=? AND `%s`=?;",
                TABLE,
                Fields.VERSION,
                Fields.MAPPING_ID,
                Fields.NAME,
                Fields.PROJECT_ID);
        final String insertSql = String.format(
                "INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?)",
                TABLE,
                Fields.VERSION,
                Fields.MAPPING_ID,
                Fields.NAME,
                Fields.PROJECT_ID);

        final Object[][] tmpUpdateParams = new Object[testCases.size()][UPDATE_PARAMS_SIZE];
        final Object[][] tmpInsertParams = new Object[testCases.size()][INSERT_PARAMS_SIZE];
        int caseNumToBeUpdated = 0;
        int caseNumToBeInserted = 0;

        for (TestCaseModel testcase : testCases) {
            final String idStr = existingCases.get(testcase.getName());
            if (idStr == null || idStr.isEmpty()) {
                // No test case with the same name, insert it.
                tmpInsertParams[caseNumToBeInserted][0] = version;
                tmpInsertParams[caseNumToBeInserted][1] = testcase.getMappingId();
                tmpInsertParams[caseNumToBeInserted][2] = testcase.getName();
                tmpInsertParams[caseNumToBeInserted][3] = projectId;
                caseNumToBeInserted++;
            } else {
                // There is an existing entry for this case, update it.
                tmpUpdateParams[caseNumToBeUpdated][0] = version;
                tmpUpdateParams[caseNumToBeUpdated][1] = testcase.getMappingId();
                tmpUpdateParams[caseNumToBeUpdated][2] = testcase.getName();
                tmpUpdateParams[caseNumToBeUpdated][3] = projectId;
                caseNumToBeUpdated++;
            }
        }

        final Object[][] updateParams = Arrays.copyOfRange(tmpUpdateParams, 0, caseNumToBeUpdated);
        final Object[][] insertParams = Arrays.copyOfRange(tmpInsertParams, 0, caseNumToBeInserted);

        System.out.println("The version is: " + version);
        System.out.println("The number of test cases to be updated: " + updateParams.length);
        System.out.println("The number of test cases to be inserted: " + insertParams.length);
        if (caseNumToBeUpdated != 0) {
            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    Db.batch(updateSql, updateParams, 500);
                    return true;
                }
            });
        }
        if (caseNumToBeInserted != 0) {
            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    Db.batch(insertSql, insertParams, 500);
                    return true;
                }
            });
        }
    }

    public Map<String, String> findAllCaseNameIdMap(long projectId) {
        Map<String, String> map = new HashMap<String, String>();
        List<TestCaseModel> testCases = findAllTestCases(projectId);
        for (TestCaseModel model : testCases) {
            map.put(model.getName(), model.getId() + "");
        }
        return map;
    }

    private List<TestCaseModel> findAllTestCases(long projectId) {
        String sql = String.format(
                "SELECT `%s`, `%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?",
                Fields.ID,
                Fields.PROJECT_ID,
                Fields.NAME,
                Fields.VERSION,
                Fields.MAPPING_ID,
                TABLE,
                Fields.PROJECT_ID);

        return find(sql, projectId);
    }
}
