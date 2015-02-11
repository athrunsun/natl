package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.TestSuite;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteModel extends Model<TestSuiteModel> {
    public static final String TABLE = "test_suite";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final TestSuiteModel me = new TestSuiteModel();

    public List<TestSuite> findTestSuites(long projectId) {
        List<TestSuite> testSuites = new ArrayList<TestSuite>();

        for(TestSuiteModel m : this.find(String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?", TABLE, Fields.ID, Fields.NAME, Fields.PROJECT_ID), projectId)) {
            testSuites.add(TestSuite.create(m));
        }

        return testSuites;
    }
}
