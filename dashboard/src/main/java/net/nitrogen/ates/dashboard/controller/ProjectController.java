package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;

import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.core.model.custom_parameter.ProjectEmailSetting;
import net.nitrogen.ates.core.model.custom_parameter.ProjectEmailSetting.Keys;
import net.nitrogen.ates.core.model.project.ProjectModel;

public class ProjectController extends Controller {
    public void index() {
        final long projectId = ControllerHelper.getProjectPrefFromCookie(this);
        setAttr("project", ProjectModel.me.findProject(projectId));
        setAttr("emailSettings", CustomParameterModel.me.getProjectEmailSettings(projectId));
        render("index.html");
    }

    public void save() {
        ProjectModel.me.findById(getParaToLong(ProjectModel.Fields.ID)).set(ProjectModel.Fields.NAME, getPara(ProjectModel.Fields.NAME))
                .set(ProjectModel.Fields.GIT_URL, getPara(ProjectModel.Fields.GIT_URL))
                .set(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME, getPara(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME))
                .set(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT, getParaToInt(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT))
                .set(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT, getPara(ProjectModel.Fields.TOTAL_TEST_CASE_COUNT)).update();

        redirect("/project");
    }

    public void saveEmailSettings() {
        final Boolean emailEnabled = getPara(Keys.EMAIL_ENABLED) == null ? false : getPara(Keys.EMAIL_ENABLED).equals("on");
        final Boolean executionStarted = getPara(Keys.SEND_WHEN_EXECUTION_STARTED) == null ? false : getPara(Keys.SEND_WHEN_EXECUTION_STARTED).equals("on");
        final Boolean executionFinished = getPara(Keys.SEND_WHEN_EXECUTION_FINISHED) == null ? false : getPara(Keys.SEND_WHEN_EXECUTION_FINISHED).equals("on");
        final String defaultEmail = getPara(Keys.DEFAULT_RECIPIENTS) == null ? "" : getPara(Keys.DEFAULT_RECIPIENTS);
        ProjectEmailSetting settings = new ProjectEmailSetting(emailEnabled, executionStarted, executionFinished, defaultEmail);
        CustomParameterModel.me.updateProjectEmailSettings(ControllerHelper.getProjectPrefFromCookie(this), settings);

        redirect("/project");
    }

    public void fetchAutomationCoverageAsJson() {
        renderJson(ProjectModel.me.automationCoverage(getParaToLong("projectId")));
    }

    public void projectDetailsAJAX() {
        long projectId = getParaToLong("projectId");
        renderJson(ProjectModel.me.projectDetailsAsMap(projectId));
    }
}
