package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectModel extends Model<ProjectModel> {
    public static final String TABLE = "project";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String JAR_NAME = "jar_name";
        public static final String JAR_WITH_DEPENDENCY_NAME = "jar_with_dependency_name";
        public static final String GIT_URL = "git_url";
        public static final String TOTAL_TEST_CASE_COUNT = "total_test_case_count";
    }

    public static final ProjectModel me = new ProjectModel();

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getName() {
        return this.getStr(Fields.NAME);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public String getJarName() {
        return this.getStr(Fields.JAR_NAME);
    }

    public void setJarName(String jarName) {
        this.set(Fields.JAR_NAME, jarName);
    }

    public String getJarWithDependencyName() {
        return this.getStr(Fields.JAR_WITH_DEPENDENCY_NAME);
    }

    public void setJarWithDependencyName(String jarWithDependencyName) {
        this.set(Fields.JAR_WITH_DEPENDENCY_NAME, jarWithDependencyName);
    }

    public String getGitUrl() {
        return this.getStr(Fields.GIT_URL);
    }

    public void setGitUrl(String gitUrl) {
        this.set(Fields.GIT_URL, gitUrl);
    }

    public int getTotalTestCaseCount() {
        return this.getInt(Fields.TOTAL_TEST_CASE_COUNT);
    }

    public void setTotalTestCaseCount(int totalTestCaseCount) {
        this.set(Fields.TOTAL_TEST_CASE_COUNT, totalTestCaseCount);
    }

    public ProjectModel findProject(long projectId){
        return me.findById(projectId);
    }

    public String findJarWithDependencyName(long projectId) {
        return this.findById(projectId).getStr(Fields.JAR_WITH_DEPENDENCY_NAME);
    }

    public List<ProjectModel> findAllProjects(){
        return this.find(String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s`",
                Fields.ID,
                Fields.NAME,
                Fields.JAR_NAME,
                Fields.JAR_WITH_DEPENDENCY_NAME,
                Fields.GIT_URL,
                Fields.TOTAL_TEST_CASE_COUNT,
                TABLE));
    }

    public Map<String, Integer> automationCoverage(long projectId) {
        Map<String, Integer> coverageData = new HashMap<>();
        int totalTestCaseCount = this.findProject(projectId).getTotalTestCaseCount();

        if(totalTestCaseCount <= 0) {
            return null;
        }else {
            coverageData.put("TOTAL", totalTestCaseCount);
            int automatedTestCaseCount = TestCaseModel.me.findTestCases(projectId).size();
            coverageData.put("AUTOMATED", automatedTestCaseCount);
            return coverageData;
        }
    }
}
