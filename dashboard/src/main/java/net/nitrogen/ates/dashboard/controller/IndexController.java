package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;

public class IndexController extends Controller {
    public void index() {
        redirect("/round");
    }
}
