package net.nitrogen.ates.dashboard.controller.admin;

import com.jfinal.core.Controller;

import net.nitrogen.ates.core.model.email.EmailModel;

public class AdminEmailController extends Controller {
    public void index() {
        setAttr("emailList", EmailModel.me.findEmails(10));
    }
}
