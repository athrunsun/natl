package net.nitrogen.ates.dashboard.controller;

import javax.servlet.http.HttpServletRequest;

import net.nitrogen.ates.core.model.feedback.FeedbackModel;

import org.joda.time.DateTime;

import com.jfinal.core.Controller;

public class AtesHelpController extends Controller {
    public void index() {
        render("index.html");
    }

    public void feedback() {
        FeedbackModel feedback = new FeedbackModel();
        feedback.set(FeedbackModel.Fields.MESSAGE, getPara("feedbackContent"))
                .set(FeedbackModel.Fields.CONTACT_INFO, getPara("feedbackEmail") + " from IP: " + getRemoteLoginUserIp(getRequest()))
                .set(FeedbackModel.Fields.TYPE, getParaToInt("feedbackType")).setCreateDate(DateTime.now());
        feedback.save();
        redirect("/help");
    }

    private String getRemoteLoginUserIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
