package net.nitrogen.ates.dashboard.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import net.nitrogen.ates.core.model.project.ProjectModel;

public class ProjectValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
//        validateRequiredString("project_name", "titleMsg", "请输入Blog标题!");
//        validateRequiredString("project_git_url", "contentMsg", "请输入Blog内容!");
//        validateRequiredString("project_jar_name", "contentMsg", "请输入Blog内容!");
//        validateRequiredString("project_jar_name_w_dep", "contentMsg", "请输入Blog内容!");
    }

    @Override
    protected void handleError(Controller controller) {
        controller.keepModel(ProjectModel.class);
        controller.render("index.html");

//        String actionKey = getActionKey();
//
//        if (actionKey.equals("/blog/save")){
//            controller.render("add.html");
//        }
//        else if (actionKey.equals("/blog/update")){
//            controller.render("edit.html");
//        }
    }
}
