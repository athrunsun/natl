package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.CustomEnvModel;

public class EnvController extends Controller {
    public void index() {
        setAttr("envList", CustomEnvModel.me.findEnvsAsModelList(ControllerHelper.getProjectPrefFromCookie(this)));
        render("index.html");
    }

    public void fetchEnvsByProjectIdAsJson() {
        renderJson(CustomEnvModel.me.findEnvsAsModelList(getParaToLong("projectId")));
    }

    public void create() {
        String envName = getPara(CustomEnvModel.Fields.NAME);
        new CustomEnvModel().set(CustomEnvModel.Fields.NAME, envName).set(CustomEnvModel.Fields.PROJECT_ID, ControllerHelper.getProjectPrefFromCookie(this)).save();
        redirect("/env");
    }

    public void delete() {
        renderText(String.valueOf(CustomEnvModel.me.deleteById(getParaToLong("envId"))));
    }
}
