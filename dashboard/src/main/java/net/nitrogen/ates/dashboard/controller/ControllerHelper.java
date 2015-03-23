package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.dashboard.constant.CookieKey;

public class ControllerHelper {
    public static long getProjectPrefFromCookie(Controller c) {
        return Long.parseLong(c.getCookie(CookieKey.PROJECT_PREF));
    }

    public static void setExecResultEnumAttr(Controller c) {
        c.setAttr("EXEC_RESULT_UNKNOWN", ExecResult.UNKNOWN.getValue());
        c.setAttr("EXEC_RESULT_SKIPPED", ExecResult.SKIPPED.getValue());
        c.setAttr("EXEC_RESULT_PASSED", ExecResult.PASSED.getValue());
        c.setAttr("EXEC_RESULT_FAILED", ExecResult.FAILED.getValue());
    }
}
