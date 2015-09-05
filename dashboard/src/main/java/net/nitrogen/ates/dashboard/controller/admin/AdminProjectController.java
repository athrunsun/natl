package net.nitrogen.ates.dashboard.controller.admin;

import net.nitrogen.ates.core.model.project.ProjectModel;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.test_case.TestCaseModel;

public class AdminProjectController extends Controller {
    public void index() {
        setAttr("projectList", ProjectModel.me.findAllProjects());
        render("index.html");
    }

    public void create() {
        new ProjectModel().set(ProjectModel.Fields.NAME, getPara(ProjectModel.Fields.NAME))
                .set(ProjectModel.Fields.GIT_URL, getPara(ProjectModel.Fields.GIT_URL))
                .set(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME, getPara(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME))
                .set(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT, getParaToInt(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT))
                .set(ProjectModel.Fields.LATEST_TEST_CASE_VERSION, TestCaseModel.generateTestCaseVersion())
                .save();

        redirect("/admin/project");
    }
}
