package net.nitrogen.ates.dashboard.controller.admin;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.ProjectModel;

public class AdminProjectController extends Controller {
    public void index(){
        setAttr("projectList", ProjectModel.me.findAllProjects());
        render("index.html");
    }

    public void create() {
        new ProjectModel().
                set(ProjectModel.Fields.NAME, getPara(ProjectModel.Fields.NAME)).
                set(ProjectModel.Fields.GIT_URL, getPara(ProjectModel.Fields.GIT_URL)).
                set(ProjectModel.Fields.JAR_NAME, getPara(ProjectModel.Fields.JAR_NAME)).
                set(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME, getPara(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME)).
                save();

        redirect("/admin/project");
    }
}
