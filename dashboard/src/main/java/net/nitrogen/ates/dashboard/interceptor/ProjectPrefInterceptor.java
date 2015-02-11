package net.nitrogen.ates.dashboard.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import net.nitrogen.ates.dashboard.constant.CookieKey;

public class ProjectPrefInterceptor implements Interceptor {
    @Override
    public void intercept(ActionInvocation ai) {
        // TODO: set default project id based on user permission
        String currentProjectId = ai.getController().getCookie(CookieKey.PROJECT_PREF, "1");

        // Set cookie to expire after 365 days
        ai.getController().setCookie(CookieKey.PROJECT_PREF, currentProjectId, 31536000);
        ai.getController().setAttr("currentProjectId", Long.parseLong(currentProjectId));
        ai.invoke();
    }
}
