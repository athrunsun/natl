package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.dashboard.constant.CookieKey;

public class ControllerHelper {
    public static long getProjectPrefFromCookie(Controller c) {
        return Long.parseLong(c.getCookie(CookieKey.PROJECT_PREF));
    }
}
