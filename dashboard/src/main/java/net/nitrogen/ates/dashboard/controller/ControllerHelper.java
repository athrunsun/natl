package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.model.CustomParameterModel;
import net.nitrogen.ates.dashboard.constant.CookieKey;
import net.nitrogen.ates.dashboard.interceptor.RawCustomParameterHandlingInterceptor;

import java.util.Map;

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

    @SuppressWarnings("unchecked")
    public static Map<String, CustomParameterModel> getRawCustomParameterMap(Controller c) {
        return (Map<String, CustomParameterModel>)c.getAttr(
                RawCustomParameterHandlingInterceptor.RAW_CUSTOM_PARAMETER_MAP_ATTR_NAME);
    }
}
