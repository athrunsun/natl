package net.nitrogen.ates.dashboard.controller;

import net.nitrogen.ates.core.model.ProjectModel;

import com.jfinal.core.Controller;

public class ProjectController extends Controller {
    public void index() {
        setAttr("project", ProjectModel.me.findProject(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void save() {
        ProjectModel.me.findById(getParaToLong(ProjectModel.Fields.ID)).set(ProjectModel.Fields.NAME, getPara(ProjectModel.Fields.NAME))
                .set(ProjectModel.Fields.GIT_URL, getPara(ProjectModel.Fields.GIT_URL))
                .set(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME, getPara(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME))
                .set(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT, getParaToInt(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT))
                .set(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT, getPara(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT)).update();

        redirect("/admin/project");
    }

    public void fetchAutomationCoverageAsJson() {
        renderJson(ProjectModel.me.automationCoverage(getParaToLong("projectId")));
    }
}
