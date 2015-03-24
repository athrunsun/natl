package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

public class TestSuiteModel extends Model<TestSuiteModel> {
    public static final String TABLE = "test_suite";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final TestSuiteModel me = new TestSuiteModel();

    public long getId() {
        return getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getName() {
        return getStr(Fields.NAME);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public long getProjectId() {
        return getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }

    public List<TestSuiteModel> findTestSuites(long projectId) {
        return this.find(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.PROJECT_ID),
                projectId);
    }
}
