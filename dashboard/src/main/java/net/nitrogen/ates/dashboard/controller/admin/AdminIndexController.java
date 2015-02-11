package net.nitrogen.ates.dashboard.controller.admin;

import com.jfinal.core.Controller;

public class AdminIndexController extends Controller {
    public void index() {
        redirect("/admin/project");
    }
}
